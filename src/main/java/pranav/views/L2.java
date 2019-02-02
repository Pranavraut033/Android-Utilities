package pranav.views;

import android.view.animation.Animation;

public interface L2 {

    interface EndAnimation extends L2 {
        void onAnimationEnd(Animation animation);
    }

    interface StartAnimation extends L2 {
        void onAnimationStart(Animation animation);
    }

    interface RepeatAnimation extends L2 {
        void onAnimationRepeat(Animation animation);
    }

    interface Events extends StartAnimation, EndAnimation, RepeatAnimation {
    }
}