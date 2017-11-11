package pranav.views;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.support.annotation.FloatRange;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import pranav.utilities.ArgbEval;

import static pranav.utilities.Animations.ANIMATION_TIME;
import static pranav.utilities.Utilities.MATCH;
import static pranav.utilities.Utilities.isFinite;

/**
 * Created on 10-07-2017 at 21:58 by Pranav Raut.
 * For QRCodeProtection
 */

public class Background extends FrameLayout  {

    @IdRes
    public static final int BACK_ID = 2146;
    private int color = 0x90000000;
    private float percent;

    private ArgbEval argbEval = new ArgbEval(0, color);
    private ObjectAnimator animator;

    private FrameLayout listener;
    @Nullable
    private Keys keys;
    private TimeInterpolator interpolator;
    private Listeners.l1<Background> l;
    private int state;

    public Background(@NonNull Context context) {
        super(context);
        init();
    }

    public Background(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setLayoutParams(MATCH);
        setFocusableInTouchMode(true);
        setFocusable(true);
        listener = new FrameLayout(getContext());
        listener.setLayoutParams(MATCH);
        super.addView(listener);
        argbEval.setObject(this);
        listener.setId(BACK_ID);
        l = new Listeners.l1<>(this);
    }

    public Background setClickListener(@Nullable final OnClickListener l) {
        listener.setOnClickListener(l);
        return this;
    }

    public void setColor(int color) {
        this.color = color;
        argbEval = new ArgbEval(0, color);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
            return onBackPress(keyCode, event);
        return super.onKeyDown(keyCode, event);
    }

    public Background setKeys(@Nullable Keys onBackPress) {
        this.keys = onBackPress;
        return this;
    }

    private boolean onBackPress(int keyCode, KeyEvent event) {
        boolean b = keys == null || keys.onBackPress(percent, keyCode, event);
        clearFocus();
        return b;
    }

    public void animateTo(@FloatRange(from = 0, to = 100) float percent) {
        if (percent != this.percent)
            if (animator == null || !animator.isStarted()) {
                animator = ObjectAnimator.ofFloat(this, "percent", 100 - percent, percent);
                animator.setDuration(ANIMATION_TIME).setInterpolator(interpolator);
                animator.addListener(l.getAnimator());
                animator.start();
            }
    }

    void addListener(L1... l1s) {
        l.addListener(l1s);
    }

    void remove(L1... l1s) {
        l.removeListener(l1s);
    }

    public void setInterpolator(TimeInterpolator interpolator) {
        this.interpolator = interpolator;
    }

    public void toggle() {
        if (!isRunning()) animateTo(100 - percent);
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(@FloatRange(from = 0f, to = 100f) float percent) {
        if (isFinite(percent)) {
            if (percent == 100) setState(VISIBLE);
            else if (percent == 0) setState(GONE);
            argbEval.getValue(percent / 100f);
            this.percent = percent;
        }
    }

    public int getState() {
        return state;
    }

    public Background setState(@vi int visibility) {
        listener.setVisibility(state = visibility);
        animator = null;
        if (visibility == VISIBLE) {
            percent = 100;
            requestFocus();
        } else percent = 0;
        return this;
    }

    public boolean isRunning() {
        return l.isRunning();
    }

    public Listeners.l1<Background> getL() {
        return l;
    }

}