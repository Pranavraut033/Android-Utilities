package pranav.views;

import android.animation.Animator;

public interface L1 {

    interface EndAnimation extends L1 {
        void onAnimationEnd(Animator animation);
    }

    interface StartAnimation extends L1 {
        void onAnimationStart(Animator animation);
    }

    interface CancelAnimation extends L1 {
        void onAnimationCancel(Animator animation);
    }

    interface RepeatAnimation extends L1 {
        void onAnimationRepeat(Animator animation);
    }

    interface Events extends StartAnimation, EndAnimation, RepeatAnimation, CancelAnimation {
    }
}