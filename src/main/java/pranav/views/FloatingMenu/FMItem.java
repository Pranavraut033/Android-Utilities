package pranav.views.FloatingMenu;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.preons.pranav.utilities.R;

import pranav.utilities.Utilities;

import static pranav.utilities.Animations.ANIMATION_TIME;
import static pranav.utilities.Animations.DI;
import static pranav.utilities.Utilities.Resources.getColoredDrawable;

/**
 * Created on 31-05-17 at 06:53 PM by Pranav Raut.
 * For QRCodeProtection
 */

@SuppressWarnings("unused")
public final class FMItem extends LinearLayout {

    private final Utilities.Resources res = new Utilities.Resources(getContext());
    private Context context = getContext();
    private TextView textView;
    private FloatingActionButton actionButton;
    private ImageView imageView;
    private long d = ANIMATION_TIME * 2 / 3;
    private boolean useCard;

    FMItem(Context context, boolean useCard) {
        super(context);
        this.useCard = useCard;
        inti();
    }

    public FMItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inti();
    }

    private void inti() {
        LayoutInflater.from(context).inflate(R.layout.floating_menu_item, this);
        textView = findViewById(R.id.optionText);
        actionButton = findViewById(R.id.optionFab);
        imageView = findViewById(R.id.menuIcon);
        if (useCard) {
            actionButton.setVisibility(GONE);
            LayoutParams params = new LayoutParams(textView.getLayoutParams());
            params.setMargins(0, 0, 0, 0);
            textView.setLayoutParams(params);
            imageView.setVisibility(VISIBLE);
            textView.setTextColor(0xb0000000);
            textView.setTextSize(16);
        }

    }

    void setOptionText(String text) {
        textView.setText(text);
    }

    @SuppressWarnings("deprecation")
    void setOptionBackground(Drawable background) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            textView.setBackground(background);
        else textView.setBackgroundDrawable(background);
    }

    void setFabSrc(Drawable src) {
        if (useCard) {
            imageView.setImageDrawable(src);
        } else
            actionButton.setImageDrawable(src);
    }

    @SuppressWarnings("deprecation")
    void setFabColor(@ColorInt int color) {
        Drawable drawable = getColoredDrawable(res.getDimen(R.dimen.menuIcon), color);
        if (useCard)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                imageView.setBackground(drawable);
            else imageView.setBackgroundDrawable(drawable);
        else
            actionButton.setBackgroundTintList(new ColorStateList(new
                    int[][]{{0}}, new int[]{color}));
    }

    void setListener(OnClickListener onClickListener) {
        if (useCard) imageView.setOnClickListener(onClickListener);
        else actionButton.setOnClickListener(onClickListener);
        textView.setOnClickListener(onClickListener);

    }

    void initialPosition(int t) {
        setScaleX(0);
        setScaleY(0);
        setTranslationX(getWidth() * .4f);
        setTranslationY(t * getHeight());
        setRotation(35);
        animate().setInterpolator(new DecelerateInterpolator());
    }

    TextView getTextView() {
        return textView;
    }

    FloatingActionButton getActionButton() {
        return actionButton;
    }

    public ImageView getImageView() {
        return imageView;
    }

    void animateOpen(int delay) {
        animate().rotation(0).scaleY(1).scaleX(1).translationX(0).setInterpolator(DI)
                .translationY(0).setDuration(d).setStartDelay(delay);
    }

    void animateClose(int delay, int t) {
        animate().rotation(35).scaleY(0).scaleX(0).translationX(getWidth() * .4f).setInterpolator(DI)
                .translationY(t * getHeight()).setDuration(d).setStartDelay(delay);
    }

}
