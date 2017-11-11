package pranav.views;

import android.animation.Animator;
import android.support.annotation.NonNull;
import android.view.animation.Animation;

import java.util.ArrayList;
import java.util.Arrays;

import pranav.views.L1;
import pranav.views.L2;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Listeners {

    public static class l1<E> {

        protected ArrayList<L1> listeners = new ArrayList<>();

        private E chain;
        private boolean running;
        private boolean ended;

        public l1() {
        }

        public l1(E e) {
            this.chain = e;
        }

        public void setChain(E chain) {
            this.chain = chain;
        }

        public E addListener(@NonNull L1... listeners) {
            this.listeners.addAll(Arrays.asList(listeners));
            return chain;
        }

        public E addListener(@NonNull ArrayList<L1> listeners) {
            this.listeners.addAll(listeners);
            return chain;
        }

        public E removeListener(@NonNull L1... listeners) {
            this.listeners.removeAll(Arrays.asList(listeners));
            return chain;
        }

        public E removeListener(@NonNull ArrayList<L1> listeners) {
            this.listeners.removeAll(listeners);
            return chain;
        }

        public Animator.AnimatorListener getAnimator() {
            return new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    ended = false;
                    running = true;
                    for (L1 l1 : listeners)
                        if (l1 instanceof L1.StartAnimation)
                            ((L1.StartAnimation) l1).onAnimationStart(animation);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    ended = true;
                    running = false;
                    for (L1 l1 : listeners)
                        if (l1 instanceof L1.EndAnimation)
                            ((L1.EndAnimation) l1).onAnimationEnd(animation);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    ended = true;
                    running = false;
                    for (L1 l1 : listeners)
                        if (l1 instanceof L1.CancelAnimation)
                            ((L1.CancelAnimation) l1).onAnimationCancel(animation);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    running = true;
                    for (L1 l1 : listeners)
                        if (l1 instanceof L1.RepeatAnimation)
                            ((L1.RepeatAnimation) l1).onAnimationRepeat(animation);
                }
            };
        }

        public boolean isEnded() {
            return ended;
        }

        public boolean isRunning() {
            return running;
        }
    }

    public static class l2<E> {

        protected ArrayList<L2> listeners = new ArrayList<>();

        private E chain;
        private boolean running;
        private boolean ended;

        public l2() {

        }

        public l2(E e) {
            this.chain = e;
        }

        public void setChain(E chain) {
            this.chain = chain;
        }

        public E addListener(@NonNull L2... listeners) {
            this.listeners.addAll(Arrays.asList(listeners));
            return chain;
        }

        public E addListener(@NonNull ArrayList<L2> listeners) {
            this.listeners.addAll(listeners);
            return chain;
        }

        public E removeListener(@NonNull L2... listeners) {
            this.listeners.removeAll(Arrays.asList(listeners));
            return chain;
        }

        public Animation.AnimationListener getAnimationListener() {
            return new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    running = true;
                    for (L2 l : listeners)
                        if (l instanceof L2.StartAnimation)
                            ((L2.StartAnimation) l).onAnimationStart(animation);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ended = true;
                    running = false;
                    for (L2 l : listeners)
                        if (l instanceof L2.EndAnimation)
                            ((L2.EndAnimation) l).onAnimationEnd(animation);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    running = true;
                    for (L2 l : listeners)
                        if (l instanceof L2.RepeatAnimation)
                            ((L2.RepeatAnimation) l).onAnimationRepeat(animation);
                }
            };
        }

        public boolean isEnded() {
            return ended;
        }

        public boolean isRunning() {
            return running;
        }
    }

}