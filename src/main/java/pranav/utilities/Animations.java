package pranav.utilities;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;
import pranav.views.L1;
import pranav.views.Listeners;

import static android.view.View.GONE;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static pranav.utilities.Utilities.isVisible;

public class Animations {

    public static final long ANIMATION_DURATION = 250;
    public static final long USER_FRIENDLY_DELAY = 2500;

    public static final Interpolator DI = new AccelerateDecelerateInterpolator();

    public static void toggleVisibility(View view) {
        animateAlpha(view, isVisible(view) ? 0 : 1);
    }

    public static void animateAlpha(final View view, final float to) {
        animateAlpha(view, to, DI);
    }

    public static void animateAlpha(final View view, final float to, @Nullable TimeInterpolator interpolator) {
        animateAlpha(view, to, ANIMATION_DURATION, interpolator);
    }

    public static void animateAlpha(View view, float to, long animationTime, @Nullable TimeInterpolator interpolator) {
        animateAlpha(view, to, animationTime, interpolator, 0);
    }

    public static void animateAlpha(final View view, final float to, long duration, @Nullable TimeInterpolator interpolator, long delay) {
        if (to == view.getAlpha()) return;
        if (!isVisible(view))
            view.setVisibility(View.VISIBLE);
        view.setAlpha(Math.abs(1 - to));
        view.animate().alpha(to).setDuration(ANIMATION_DURATION).setListener(new Animator.AnimatorListener() {
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
        valueAnimator.setDuration(ANIMATION_DURATION);
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.start();
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public static class AnimatingDimensions extends Listeners.l2<AnimatingDimensions> {
        public static final int ANIMATE_HEIGHT = 0;
        public static final int ANIMATE_WIDTH = 1;
        private final View view;
        private final float initialDimen;
        private final float targetDimen;
        private boolean toFinal = true;
        private int mode = ANIMATE_HEIGHT;
        private Interpolator interpolator = DI;
        private boolean running;

        private long duration = -1;
        private long delay;

        public AnimatingDimensions(View view, float initialDimen, float targetDimen) {
            this(view, true, initialDimen, targetDimen);
        }

        public AnimatingDimensions(View view, boolean toFinal, float initialDimen, float targetDimen) {
            this.view = view;
            this.toFinal = toFinal;
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
            new AnimatingDimensions(v, oldH, newH).animate(!b);
        }

        public void animate(boolean show) {
            this.toFinal = show;
            animate();
        }

        public void animate() {
            final Animation animation;
            switch (mode) {
                case ANIMATE_HEIGHT:
                    view.getLayoutParams().height = (int) (toFinal ? initialDimen : targetDimen);
                    break;
                case ANIMATE_WIDTH:
                    view.getLayoutParams().width = (int) (toFinal ? initialDimen : targetDimen);
                    break;
            }
            if (!isVisible(view)) view.setVisibility(View.VISIBLE);
            if (toFinal) {
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
            new Handler().postDelayed(() -> {
                view.startAnimation(animation);
                toFinal = !toFinal;
            }, delay);
        }

        public AnimatingDimensions setInterpolator(Interpolator interpolator) {
            this.interpolator = interpolator;
            return this;
        }

        public void setToFinal(boolean toFinal) {
            this.toFinal = toFinal;
        }

        @a
        public int getMode() {
            return mode;
        }

        public AnimatingDimensions setMode(@a int mode) {
            this.mode = mode;
            return this;
        }

        public boolean isRunning() {
            return running;
        }

        public AnimatingDimensions setDuration(long duration) {
            this.duration = duration;
            return this;
        }

        public AnimatingDimensions setDelay(long delay) {
            this.delay = delay;
            return this;
        }

        public boolean toFinal() {
            return toFinal;
        }

        @IntDef(value = {ANIMATE_HEIGHT, ANIMATE_WIDTH})
        private @interface a {
        }
    }

    @SuppressWarnings("unused")
    public static class AnimateStatusBar extends Listeners.l1<AnimateStatusBar> {

        private final Window window;
        TimeInterpolator interpolator;
        private long duration = ANIMATION_DURATION;
        private boolean lightStatusBar;

        public AnimateStatusBar(Window window) {
            this.window = window;
            setChain(this);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public synchronized void animate(int colorFrom, int colorTo) {
            animate(colorFrom, colorTo, false);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public synchronized void animate(int colorFrom, int colorTo, boolean reverse) {
            if (reverse) {
                colorFrom = colorTo + colorFrom;
                colorTo = colorFrom - colorTo;
                colorFrom = colorFrom - colorTo;
            }
            AnimatingColor animatingColor = new AnimatingColor(colorFrom, colorTo);
            animatingColor.setColorChangeListener(color -> {
                if (window != null) window.setStatusBarColor(color);
            });
            animatingColor.addListener(listeners);
            animatingColor.setDuration(duration);
            animatingColor.setInterpolator(interpolator);
            animatingColor.start();
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        public synchronized void setLightStatusBar() {
            lightStatusBar = true;
            pranav.utilities.Utilities.setLightStatusBar(window);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        public synchronized void clearLightStatusBar() {
            lightStatusBar = false;
            pranav.utilities.Utilities.clearLightStatusBar(window);
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

        public AnimateStatusBar setDuration(long duration) {
            this.duration = duration;
            return this;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public int getCurrentStatusColor() {
            return window.getStatusBarColor();
        }
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public static class AnimatingColor extends Listeners.l1<AnimatingColor> implements Cloneable {

        public boolean ended;
        private int colorTo;
        private int colorFrom;
        @Nullable
        private ColorChangeListener colorChangeListener;
        private ArrayList<View> objects = new ArrayList<>();
        private long duration = ANIMATION_DURATION;
        private long delay = 0;
        private TimeInterpolator interpolator;
        private int value;
        private boolean running = false;
        private L1[] listeners = new L1[0];

        public AnimatingColor() {
            this(0, 0);
        }

        public AnimatingColor(AnimatingColor animatingColor) {
            setChain(this);
            setColors(animatingColor.colorTo, animatingColor.colorFrom)
                    .setDelay(animatingColor.delay).setDuration(animatingColor.duration)
                    .setColorChangeListener(animatingColor.colorChangeListener);
            objects = animatingColor.objects;
        }

        public AnimatingColor(int colorFrom, int colorTo) {
            setChain(this);
            this.colorFrom = value = colorFrom;
            this.colorTo = colorTo;
        }

        public AnimatingColor(int colorTo, View... objects) {
            setChain(this);
            this.colorTo = colorTo;
            setObjects(objects);
        }

        public void animateTo() {
            for (View v : objects) {
                int i = 0;
                if (v instanceof TextView)
                    i = ((TextView) v).getCurrentTextColor();
                else if (v instanceof Toolbar) {
                    Toolbar v1 = (Toolbar) v;
                    for (int j = 0; j < v1.getChildCount(); j++)
                        if (v1.getChildAt(j) instanceof TextView)
                            i = ((TextView) v1.getChildAt(j)).getCurrentTextColor();
                } else
                    i = v.getSolidColor();
                setColors(i, colorTo).start();
            }
        }

        public void start(int colorFrom, int colorTo) {
            setColors(colorFrom, colorTo).start();
        }

        public void start() {
            start(false);
        }

        public void start(boolean reverse) {
            int colorFrom = this.colorFrom, colorTo = this.colorTo;
            if (reverse) {
                colorFrom = colorTo + colorFrom;
                colorTo = colorFrom - colorTo;
                colorFrom = colorFrom - colorTo;
            }
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.addUpdateListener(animator -> {
                value = (int) animator.getAnimatedValue();
                if (!objects.isEmpty()) {
                    for (View o : objects)
                        if (o != null) {
                            if (o instanceof ImageView)
                                ((ImageView) o).setColorFilter(value, PorterDuff.Mode.SRC_ATOP);
                            else if (o instanceof TextView)
                                ((TextView) o).setTextColor(value);
                            else if (o instanceof CardView)
                                ((CardView) o).setCardBackgroundColor(value);
                            else if (o instanceof Toolbar)
                                ((Toolbar) o).setTitleTextColor(value);
                            else
                                o.setBackgroundColor(value);
                        }
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
        public View[] getObjects() {
            return objects.toArray(new View[0]);
        }

        public AnimatingColor removeObjects(View... objects) {
            if (objects.length != 0)
                this.objects.removeAll(Arrays.asList(objects));
            return this;
        }

        public AnimatingColor setObjects(View... objects) {
            if (objects.length != 0)
                this.objects.addAll(Arrays.asList(objects));
            return this;
        }

        public interface ColorChangeListener {
            void onColorChanged(int color);
        }
    }

    public static class Utilities {
        public static class ZoomOutPageTransformer implements ViewPager.PageTransformer {
            private static final float MIN_SCALE = 0.85f;
            private static final float MIN_ALPHA = 0.5f;

            public void transformPage(@NonNull View view, float position) {
                int pageWidth = view.getWidth();
                int pageHeight = view.getHeight();

                if (position < -1) { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    view.setAlpha(0);

                } else if (position <= 1) { // [-1,1]
                    // Modify the default slide transition to shrink the page as well
                    float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                    float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                    float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                    if (position < 0) {
                        view.setTranslationX(horzMargin - vertMargin / 2);
                    } else {
                        view.setTranslationX(-horzMargin + vertMargin / 2);
                    }

                    // Scale the page down (between MIN_SCALE and 1)
                    view.setScaleX(scaleFactor);
                    view.setScaleY(scaleFactor);

                    // Fade the page relative to its size.
                    view.setAlpha(MIN_ALPHA +
                            (scaleFactor - MIN_SCALE) /
                                    (1 - MIN_SCALE) * (1 - MIN_ALPHA));

                } else { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    view.setAlpha(0);
                }
            }
        }

    }
}
