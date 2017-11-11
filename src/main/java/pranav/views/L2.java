package pranav.views;

import android.view.animation.Animation;

public interface L2 {

    interface EndAnimation extends L1 {
        void onAnimationEnd(Animation animation);
    }

    interface StartAnimation extends L1 {
        void onAnimationStart(Animation animation);
    }

    interface RepeatAnimation extends L1 {
        void onAnimationRepeat(Animation animation);
    }

    interface Events extends StartAnimation, EndAnimation, RepeatAnimation {
    }
}