package pranav.views;

import android.animation.Animator;
import android.animation.ValueAnimator;

/**
 * Created on 10-11-2017 at 15:39 by Pranav Raut.
 * For QRCodeProtection
 */

public abstract class AnimationListener implements ValueAnimator.AnimatorListener {
    @Override
    public abstract void onAnimationStart(Animator animation);

    @Override
    public abstract void onAnimationEnd(Animator animation);

    @Override
    public void onAnimationCancel(Animator animation) {
        onAnimationEnd(animation);
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
