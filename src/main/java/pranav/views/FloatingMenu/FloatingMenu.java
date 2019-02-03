package pranav.views.FloatingMenu;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.transition.ArcMotion;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.preons.pranav.utilities.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import pranav.utilities.Animations;
import pranav.utilities.Utilities;

import static pranav.utilities.Animations.ANIMATION_DURATION;
import static pranav.utilities.Animations.animateAlpha;
import static pranav.utilities.Animations.animateRotate;
import static pranav.utilities.Log.TAG;
import static pranav.utilities.Utilities.Colors.blend;
import static pranav.utilities.Utilities.Colors.changeAlpha;
import static pranav.utilities.Utilities.Colors.isBright;
import static pranav.utilities.Utilities.getReverseArray;


/**
 * Created on 30-05-17 at 11:57 PM by Pranav Raut.
 * For QRCodeProtection
 */

@SuppressWarnings("unused")
public final class FloatingMenu extends FrameLayout {

    private final AttributeSet attrs;
    private final float c;
    private Context context = getContext();
    private Utilities.Resources res;
    private ArcMotion arcMotion = null;
    private Drawable drawable;
    private Window window;
    private ArrayList<FMItem> menuItems = new ArrayList<>();
    private ArrayList<FMGroupHelper> helpers = new ArrayList<>();
    private ArrayList<FMGroup> groups = new ArrayList<>();
    private ItemAnimation itemAnimation = null;
    private Animations.AnimateStatusBar statusBar;
    private Animations.AnimatingColor CVAnimator = new Animations.AnimatingColor(),
            mSRCAnimator = new Animations.AnimatingColor(), MBAnimator = new Animations.AnimatingColor();
    private LinearLayout mainContainer;
    private CardView cardView;
    private FMHelper details;
    private ImageView background;
    private FloatingActionButton mainBtn;
    private int stateToSave;

    private Animations.AnimatingColor.ColorChangeListener listener = new Animations.AnimatingColor.ColorChangeListener() {
        @Override
        public void onColorChanged(int color) {
            mainBtn.setBackgroundTintList(new ColorStateList(new
                    int[][]{{0}}, new int[]{color}));
        }
    }, listener1 = new Animations.AnimatingColor.ColorChangeListener() {
        @Override
        public void onColorChanged(int color) {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    };
    private int oColor;
    private boolean visible = false;
    private boolean isMenuGroup = false;
    private boolean useCard = true;
    private float i;
    private float nXc, nYc;
    private boolean once = true;
    private float oXc, oYc, nXb, nYb, oXb, oYb, v;
    private Interpolator interpolator = Animations.DI;
    public final OnClickListener mainBtnListener = v -> toggleMenu(),
            backgroundListener = v -> {
                visible = true;
                mainBtnListener.onClick(v);
            };

    public FloatingMenu(Context context) {
        this(context, null);
    }

    public FloatingMenu(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.attrs = attrs;
        res = new Utilities.Resources(context);
        c = res.getDimen(R.dimen.pad28dp);
        inti();
        int childCount = getChildCount();
        Log.i(TAG, "FloatingMenu: " + childCount);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        //begin boilerplate code that allows parent classes to save state
        Parcelable superState = super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", superState);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle b = (Bundle) state;
            state = b.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }

    private void inti() {
        inflate(context, R.layout.floating_menu, this);
        mainContainer = findViewById(R.id.mainContainer);
        cardView = findViewById(R.id.menuCard);
        i = -res.getDimen(R.dimen.pad32dp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            arcMotion = new ArcMotion();
        }
        if (attrs != null)
            a();
        setFocusableInTouchMode(true);
        if (useCard) cardView();
    }

    private void a() {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FloatingMenu);
        int numOptions = typedArray.getInt(R.styleable.FloatingMenu_numOption, 3);
        FMHelper helper = new FMHelper(numOptions, context);
        useCard = typedArray.getBoolean(R.styleable.FloatingMenu_useCard, true);
        int color = typedArray.getColor(R.styleable.FloatingMenu_baseOptionColor, helper.getMainBtnBaseColor()),
                color1 = typedArray.getColor(R.styleable.FloatingMenu_statusBarColor, helper.getStatusBarColor());
        Drawable drawable = typedArray.getDrawable(R.styleable.FloatingMenu_backgroundColor);
        if (drawable == null) drawable = helper.getImageBackground();
        int ref = typedArray.getResourceId(R.styleable.FloatingMenu_optionTexts, 0),
                ref2 = typedArray.getResourceId(R.styleable.FloatingMenu_optionIcons, 0);
        String[] strings = ref == 0 ? helper.getOptionTexts() : context.getResources().getStringArray(ref);
        if (ref2 != 0) helper.setOptionBtnRes(context.getResources().getStringArray(ref2));
        float radius = typedArray.getDimension(R.styleable.FloatingMenu_cardRadius, res.getPx(2));
        cardView.setRadius(radius);
        helper.setUseCard(useCard)
                .setMainBtnBaseColor(color)
                .setImageBackground(drawable)
                .setStatusBarColor(color1)
                .setOptionTexts(strings);
        build(helper);
        typedArray.recycle();
    }

    public void build(FMHelper helper) {
        mainContainer.removeAllViews();
        add(helper);
    }

    public void add(FMHelper details) {
        this.details = details;
        if (useCard)
            cardView.setCardBackgroundColor(details.getMainBtnBaseColor());
        window = details.getWindow();
        statusBar = new Animations.AnimateStatusBar(window);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            oColor = window.getStatusBarColor();
        menuItems();
        background();
        mainBtn();
    }

    public void build(FMGroupHelper... groupHelpers) {
        mainContainer.removeAllViews();
        add(groupHelpers);
    }

    public void add(FMGroupHelper... groupDetails) {
        groupDetails = getReverseArray(groupDetails);
        isMenuGroup = true;
        for (FMGroupHelper groupDetail : groupDetails) {
            groupDetail.setUseCard(useCard);
            FMGroup group = new FMGroup(context, groupDetail);
            helpers.add(groupDetail);
            groups.add(group);
            add(groupDetail.getDetails());
            mainContainer.addView(group.add());
        }
    }

    public FloatingActionButton getMainBtn() {
        return mainBtn;
    }

    private void cardView() {
        cardView.setCardElevation(res.getDimen(R.dimen.pad4dp));
        cardView.setVisibility(INVISIBLE);
        mainContainer.setPadding(0, 0, 0, 0);
        cardView.setTranslationX(i);
        cardView.setTranslationY(i);
    }

    private void menuItems() {
        for (int i = 0; i < details.getNumOptions(); i++) {
            FMItem item = new FMItem(context, useCard);
            item.setFabColor(details.getOptionBtnColors()[i]);
            item.setFabSrc(details.getOptionBtnRes()[i]);
            item.setOptionText(details.getOptionTexts()[i]);
            if (!useCard)
                item.setOptionBackground(details.getOptionTextBackground());
            item.setListener(details.getListeners()[i]);
            menuItems.add(item);
            if (!isMenuGroup)
                mainContainer.addView(item);
            else groups.get(groups.size() - 1).addItem(item);
            item.setGravity(useCard ? Gravity.START : Gravity.END);
        }
        if (!isUseCard()) {
            if (itemAnimation != null) itemAnimation.initialPosition(toArray());
            else for (int i = 0; i < menuItems.size(); i++)
                menuItems.get(i).initialPosition(menuItems.size() - i);
            for (FMGroup group : groups)
                group.initial();
        }
    }

    private void background() {
        background = findViewById(R.id.floatingMenuBackground);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            background.setBackground(details.getImageBackground());
        else background.setImageDrawable(details.getImageBackground());
        background.setOnClickListener(backgroundListener);
    }

    private void mainBtn() {
        drawable = details.getMainBtnRes();
        mainBtn = findViewById(R.id.mainFabBtn);
        mainBtn.setImageDrawable(details.getMainBtnRes());
        mainBtn.setBackgroundTintList(new ColorStateList(new
                int[][]{{0}}, new int[]{details.getMainBtnBaseColor()}));
        mainBtn.setOnClickListener(mainBtnListener);
        MBAnimator.setDuration(ANIMATION_DURATION);
        MBAnimator.setColorChangeListener(listener);
        mSRCAnimator.setDuration(ANIMATION_DURATION);
        mSRCAnimator.setColorChangeListener(listener1);
    }

    public synchronized void toggleMenu() {
        if (visible) hide();
        else show();
    }

    private void show() {
        background.setOnClickListener(backgroundListener);
        mainBtn.setOnClickListener(null);
        if (events != null) events.onOpen();
        requestFocus();
        visible = true;
        Animations.animateAlpha(background, 1);
        if (!isUseCard())
            mainBtnShow();
        statusBarShow();
        if (!isUseCard()) {
            if (itemAnimation != null) itemAnimation.openAnimation(toArray());
            else {
                FMItem[] items = getReverseArray(toArray());
                for (int i = 0; i < items.length; i++)
                    items[i].animateOpen(i * 25);
            }
            for (FMGroup group : groups)
                group.animateOpen();
        } else {
            if (once) once();
            cardView.setX(oXc);
            cardView.setY(oYc);
            mainContainer.setAlpha(0);
            cardView.setVisibility(VISIBLE);
            mainContainer.setTranslationY(oYb - nYb);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(cardView, "x", "y",
                        arcMotion.getPath(oXc, oYc, nXc, nYc))
                        .setDuration(ANIMATION_DURATION);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(mainBtn, "x", "y",
                        arcMotion.getPath(oXb, oYb, nXb, nYb))
                        .setDuration(ANIMATION_DURATION);
                Animator anim = ViewAnimationUtils.createCircularReveal(
                        cardView, cardView.getWidth() / 2, cardView.getHeight() / 2, c, v)
                        .setDuration(ANIMATION_DURATION);
                animateAlpha(mainBtn, 0, ANIMATION_DURATION - 50, interpolator);
                animateAlpha(cardView, 1, interpolator);
                anim.setInterpolator(interpolator);
                anim.start();
                animator.setInterpolator(interpolator);
                animator.start();
                animator2.setInterpolator(interpolator);
                animator2.start();
            } else {
                cardView.setScaleX(0);
                cardView.setScaleY(0);
                cardView.animate().alpha(1).scaleY(1).scaleX(1).x(nXc).y(nYc).setInterpolator(interpolator)
                        .setDuration(ANIMATION_DURATION).start();
                mainBtn.animate().alpha(0).x(nXb).y(nYb).setInterpolator(interpolator)
                        .setDuration(ANIMATION_DURATION).start();
            }
            CVAnimator.setColors(details.getMainBtnBaseColor(), 0xffffffff).start();
            mainContainer.animate().translationY(0).setInterpolator(interpolator)
                    .setDuration((long) (ANIMATION_DURATION / 1.5)).setStartDelay(ANIMATION_DURATION / 4).start();
            animateAlpha(mainContainer, 1, (long) (ANIMATION_DURATION * 1.2), interpolator);
        }
    }

    private void once() {
        nXc = cardView.getX();
        nYc = cardView.getY();
        oXc = mainBtn.getX() - cardView.getWidth() / 2 + mainBtn.getWidth() / 2;
        oYc = mainBtn.getY() - cardView.getHeight() / 2 + mainBtn.getWidth() / 2;
        oXb = mainBtn.getX();
        oYb = mainBtn.getY();
        nXb = cardView.getX() + cardView.getWidth() / 2 - mainBtn.getHeight() / 2;
        nYb = cardView.getY() + cardView.getHeight() / 2 - mainBtn.getHeight() / 2;
        v = (float) Math.hypot(cardView.getWidth() / 2,
                cardView.getHeight() / 2);
        CVAnimator.setColorChangeListener(cardView::setCardBackgroundColor);
        CVAnimator.setDuration(ANIMATION_DURATION);
        once = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN && visible) {
            toggleMenu();
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }

    private void statusBarShow() {
        if (details.isAnimatedStatusBar()) {
            int t = details.getStatusBarColor();
            int tC = blend(oColor, t);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                statusBar.animate(oColor, changeAlpha(tC, 1));
            if (window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && isBright(Color.alpha(tC) != 0xff ? blend(tC, 0xffffffff) : tC, .8f))
                window.getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private void mainBtnShow() {
        animateRotate(mainBtn, 135, new DecelerateInterpolator());
        MBAnimator.setColors(details.getMainBtnBaseColor(), details.getMainBtnOpenColor())
                .start();
        if (details.isTintedFabSrc()) {
            boolean b = isBright(details.getMainBtnOpenColor());
            mSRCAnimator.setColors(b ? 0xbbffffff : 0xbb000000, b ? 0xbb000000 : 0xbbffffff).start();
        }

        background.setOnClickListener(null);
        mainBtn.setOnClickListener(mainBtnListener);
    }

    private void hide() {
        background.setOnClickListener(null);
        mainBtn.setOnClickListener(mainBtnListener);
        if (events != null) events.onClose();
        clearFocus();
        visible = false;
        Animations.animateAlpha(background, 0);
        if (!isUseCard())
            mainBtnHide();
        if (details.isAnimatedStatusBar()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                statusBar.animate(changeAlpha(blend(details.getStatusBarColor(), oColor), 1), oColor);
        }
        if (!isUseCard()) {
            if (itemAnimation != null) itemAnimation.closeAnimation(toArray());
            else for (int i = 0; i < menuItems.size(); i++)
                menuItems.get(i).animateClose(i * 25, menuItems.size() - i);
            for (FMGroup group : groups)
                group.animateClose();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cardView.setVisibility(VISIBLE);
                ObjectAnimator animator = ObjectAnimator.ofFloat(cardView, "x", "y",
                        arcMotion.getPath(nXc, nYc, oXc, oYc))
                        .setDuration(ANIMATION_DURATION);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(mainBtn, "x", "y",
                        arcMotion.getPath(nXb, nYb, oXb, oYb))
                        .setDuration(ANIMATION_DURATION);
                Animator anim = ViewAnimationUtils.createCircularReveal(
                        cardView, cardView.getWidth() / 2, cardView.getHeight() / 2, v, c)
                        .setDuration(ANIMATION_DURATION);
                animateAlpha(mainBtn, 1, ANIMATION_DURATION - 50, interpolator);
                animateAlpha(cardView, 0, interpolator);
                anim.setInterpolator(interpolator);
                anim.start();
                animator.setInterpolator(interpolator);
                animator.start();
                animator2.setInterpolator(interpolator);
                animator2.start();
            } else {
                cardView.animate().alpha(0).scaleY(0).scaleX(0).x(oXc).y(oYc).setInterpolator(interpolator)
                        .setDuration(ANIMATION_DURATION).start();
                mainBtn.animate().alpha(1).x(oXb).y(oYb).setInterpolator(interpolator)
                        .setDuration(ANIMATION_DURATION).start();
            }
            CVAnimator.setColors(0xffffffff, details.getMainBtnBaseColor()).start();
            animateAlpha(mainContainer, 0, interpolator);
        }
    }

    private void mainBtnHide() {
        animateRotate(mainBtn, 0, interpolator);
        if (details.isTintedFabSrc()) {
            boolean b = isBright(details.getMainBtnBaseColor());
            mSRCAnimator.setColors(b ? 0x90000000 : 0x90ffffff, b ? 0x90ffffff : 0x90000000).start();
        }
        MBAnimator.setColors(details.getMainBtnOpenColor(), details.getMainBtnBaseColor()).start();
    }

    @NonNull
    private FMItem[] toArray() {
        return menuItems.toArray(new FMItem[menuItems.size()]);
    }

    @Nullable
    public ItemAnimation getItemAnimation() {
        return itemAnimation;
    }

    public void setItemAnimation(@Nullable ItemAnimation itemAnimation) {
        this.itemAnimation = itemAnimation;
    }

    public boolean isUseCard() {
        return useCard;
    }

    public void setUseCard(boolean useCard) {
        this.useCard = useCard;
    }

    public ArrayList<FMGroup> getGroups() {
        return groups;
    }

    public ArrayList<FMGroupHelper> getHelpers() {
        return helpers;
    }

    /**
     * @return recent used details
     */
    public FMHelper getDetails() {
        return details;
    }

    public void lift(float height) {
        liftFor(height, 0L);
    }

    public void liftFor(float height, long duration) {
        animate().translationY(-height).setDuration(ANIMATION_DURATION)
                .setInterpolator(interpolator).start();
        if (duration > 0)
            new Handler().postDelayed(() -> animate().translationY(0).setDuration(ANIMATION_DURATION)
                    .setInterpolator(interpolator).start(), duration);
    }

    public FloatingMenu setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
        return this;
    }

    @Nullable
    Events events = null;

    public void setEvents(@Nullable Events events) {
        this.events = events;
    }

    public interface Events {
        void onOpen();

        void onClose();
    }
}