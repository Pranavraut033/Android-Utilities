package pranav.utilities;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;

import pranav.views.L1;
import pranav.views.Listeners;

import static android.view.View.GONE;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static pranav.utilities.Utilities.isVisible;

public class Animations {
    public static final long ANIMATION_TIME = 250;
    public static final long USER_FRIENDLY_DELAY = 2500;

    public static final Interpolator DI = new OvershootInterpolator(.8f);

    public static void animateAlpha(View view) {
        animateAlpha(view, isVisible(view) ? 0 : 1);
    }

    public static void animateAlpha(final View view, final float to) {
        animateAlpha(view, to, DI);
    }

    public static void animateAlpha(final View view, final float to, @Nullable TimeInterpolator interpolator) {
        animateAlpha(view, to, ANIMATION_TIME, interpolator);
    }

    public static void animateAlpha(View view, float to, long animationTime, @Nullable TimeInterpolator interpolator) {
        animateAlpha(view, to, animationTime, interpolator, 0);
    }

    public static void animateAlpha(final View view, final float to, long duration, @Nullable TimeInterpolator interpolator, long delay) {
        if (!isVisible(view))
            view.setVisibility(View.VISIBLE);
        view.setAlpha(Math.abs(1 - to));
        view.animate().alpha(to).setDuration(ANIMATION_TIME).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (to == 0) view.setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        }).setStartDelay(delay).setInterpolator(interpolator).setDuration(duration);
    }

    public static void animateRotate(View view, float angle, Interpolator interpolator) {
        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(view, "Rotation", 0, angle);
        valueAnimator.setDuration(ANIMATION_TIME);
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.start();
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public static class AnimatingParameter extends Listeners.l2<AnimatingParameter> {
        public static final int ANIMATE_HEIGHT = 0;
        public static final int ANIMATE_WIDTH = 1;
        private final View view;
        private final float initialDimen;
        private final float targetDimen;
        private boolean toInit = true;
        private int mode = ANIMATE_HEIGHT;
        private Interpolator interpolator = DI;
        private boolean running;

        private long duration = -1;
        private long delay;

        public AnimatingParameter(View view, float initialDimen, float targetDimen) {
            this(view, true, initialDimen, targetDimen);
        }

        public AnimatingParameter(View view, boolean toInit, float initialDimen, float targetDimen) {
            this.view = view;
            this.toInit = toInit;
            this.initialDimen = (int) (initialDimen <= 0 ? 1 : initialDimen);
            this.targetDimen = (int) targetDimen;
        }

        public static void animateHeight(View v, int top, int oldTop, int bottom, int oldBottom) {
            int oldH = oldBottom - oldTop;
            int newH = bottom - top;
            animateHeight(v, oldH, newH);
        }

        public static void animateHeight(View v, int oldH, int newH) {
            boolean b;
            if (b = oldH > newH) {
                int t = oldH;
                oldH = newH;
                newH = t;
            }
            new AnimatingParameter(v, oldH, newH).animate(!b);
        }

        public AnimatingParameter animate(boolean b) {
            this.toInit = b;
            return animate();
        }

        public AnimatingParameter animate() {
            final Animation animation;
            switch (mode) {
                case ANIMATE_HEIGHT:
                    view.getLayoutParams().height = (int) (toInit ? initialDimen : targetDimen);
                    break;
                case ANIMATE_WIDTH:
                    view.getLayoutParams().width = (int) (toInit ? initialDimen : targetDimen);
                    break;
            }
            if (!isVisible(view)) view.setVisibility(View.VISIBLE);
            if (toInit) {
                animation = new Animation() {
                    @Override
                    protected void applyTransformation(float interpolatedTime, Transformation t) {
                        int i = (int) (initialDimen + ((targetDimen - initialDimen) * interpolatedTime));
                        switch (mode) {
                            case ANIMATE_HEIGHT:
                                if (interpolatedTime == 1)
                                    view.getLayoutParams().height = WRAP_CONTENT;
                                else if (i >= 0) view.getLayoutParams().height = i;
                                break;
                            case ANIMATE_WIDTH:
                                if (interpolatedTime == 1)
                                    view.getLayoutParams().width = WRAP_CONTENT;
                                else if (i >= 0) view.getLayoutParams().width = i;
                                break;
                        }
                        view.requestLayout();
                    }

                    @Override
                    public boolean willChangeBounds() {
                        return true;
                    }
                };
            } else {

                animation = new Animation() {
                    @Override
                    protected void applyTransformation(float interpolatedTime, Transformation t) {
                        int i = (int) (targetDimen - ((targetDimen - initialDimen) * interpolatedTime));
                        if (i >= 0) {
                            if (interpolatedTime == 0) return;
                            switch (mode) {
                                case ANIMATE_HEIGHT:
                                    view.getLayoutParams().height = i;
                                    break;
                                case ANIMATE_WIDTH:
                                    view.getLayoutParams().width = i;
                                    break;
                            }
                            view.requestLayout();
                        }
                    }

                    @Override
                    public boolean willChangeBounds() {
                        return true;
                    }
                };
            }
            animation.setInterpolator(interpolator);
            animation.setAnimationListener(getAnimationListener());
            animation.setDuration(duration == -1 ? (long) (targetDimen - initialDimen /
                    view.getContext().getResources().getDisplayMetrics().density) : duration);
            new Handler().postDelayed(() -> view.startAnimation(animation), delay);
            return this;
        }

        public AnimatingParameter setInterpolator(Interpolator interpolator) {
            this.interpolator = interpolator;
            return this;
        }

        public void setToInit(boolean toInit) {
            this.toInit = toInit;
        }

        @a
        public int getMode() {
            return mode;
        }

        public AnimatingParameter setMode(@a int mode) {
            this.mode = mode;
            return this;
        }

        public boolean isRunning() {
            return running;
        }

        public AnimatingParameter setDuration(long duration) {
            this.duration = duration;
            return this;
        }

        public AnimatingParameter setDelay(long delay) {
            this.delay = delay;
            return this;
        }

        @IntDef(value = {ANIMATE_HEIGHT, ANIMATE_WIDTH})
        private @interface a {
        }
    }

    @SuppressWarnings("unused")
    public static class AnimateStatusBar extends Listeners.l1<AnimateStatusBar> {

        private final Window window;
        TimeInterpolator interpolator;
        private long duration = ANIMATION_TIME;
        private boolean lightStatusBar;

        public AnimateStatusBar(Window window) {
            this.window = window;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public synchronized void animate(int colorFrom, int colorTo) {
            AnimatingColor animatingColor = new AnimatingColor(colorFrom, colorTo);
            animatingColor.setColorChangeListener(color -> {
                if (window != null) window.setStatusBarColor(color);
            });
            animatingColor.addListener(listeners);
            animatingColor.setDuration(duration);
            animatingColor.setInterpolator(interpolator);
            animatingColor.start();
        }

        public synchronized void setLightStatusBar() {
            lightStatusBar = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Utilities.setLightStatusBar(window);
        }

        public synchronized void clearLightStatusBar() {
            lightStatusBar = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                Utilities.clearLightStatusBar(window);
        }

        public AnimateStatusBar setInterpolator(TimeInterpolator interpolator) {
            this.interpolator = interpolator;
            return this;
        }

        public boolean isLightStatusBar() {
            return lightStatusBar;
        }

        public void setColor(@ColorInt int color) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                window.setStatusBarColor(color);
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public static class AnimatingColor extends Listeners.l1<AnimatingColor> implements Cloneable {

        public boolean ended;
        private int colorTo;
        private int colorFrom;
        @Nullable
        private ColorChangeListener colorChangeListener;
        @Nullable
        private View object;
        private long duration = ANIMATION_TIME;
        private long delay = 0;
        private TimeInterpolator interpolator;
        private int value;
        private boolean running = false;
        private L1[] listeners = new L1[0];

        public AnimatingColor() {
            this(0, 0);
        }

        public AnimatingColor(AnimatingColor animatingColor) {
            setColors(animatingColor.colorTo, animatingColor.colorFrom)
                    .setDelay(animatingColor.delay).setDuration(animatingColor.duration)
                    .setObject(animatingColor.object).setColorChangeListener(animatingColor.colorChangeListener);
            setChain(this);
        }

        public AnimatingColor(int colorFrom, int colorTo) {
            this.colorFrom = value = colorFrom;
            this.colorTo = colorTo;
        }

        public void start(int colorFrom, int colorTo) {
            setColors(colorFrom, colorTo).start();
        }

        public void start() {
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.addUpdateListener(animator -> {
                value = (int) animator.getAnimatedValue();
                if (object != null) {
                    if (object instanceof ImageView)
                        ((ImageView) object).setColorFilter(value, PorterDuff.Mode.SRC_ATOP);
                    else object.setBackgroundColor(value);
                }
                if (colorChangeListener != null) colorChangeListener.onColorChanged(value);
            });
            colorAnimation.addListener(getAnimator());
            colorAnimation.setStartDelay(delay);
            colorAnimation.setDuration(duration);
            colorAnimation.setInterpolator(interpolator);
            colorAnimation.start();
        }

        /**
         * @return recent known animated Value
         */
        public int getValue() {
            return value;
        }

        public AnimatingColor setColors(int colorFrom, int colorTo) {
            this.colorFrom = value = colorFrom;
            this.colorTo = colorTo;
            return this;
        }

        public AnimatingColor setColorChangeListener(@Nullable ColorChangeListener colorChangeListener) {
            this.colorChangeListener = colorChangeListener;
            return this;
        }

        public long getDuration() {
            return duration;
        }

        public AnimatingColor setDuration(long duration) {
            this.duration = duration;
            return this;
        }

        public AnimatingColor setDelay(long delay) {
            this.delay = delay;
            return this;
        }

        public void startDelayed(long delay) {
            this.delay = delay;
            start();
        }

        public AnimatingColor setInterpolator(TimeInterpolator interpolator) {
            this.interpolator = interpolator;
            return this;
        }

        @Nullable
        public View getObject() {
            return object;
        }

        public AnimatingColor setObject(@Nullable View object) {
            this.object = object;
            return this;
        }

        public interface ColorChangeListener {
            void onColorChanged(int color);
        }
    }

}
