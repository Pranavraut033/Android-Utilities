package pranav.views;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Point;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import pranav.utilities.Animations;
import pranav.utilities.Utilities;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static pranav.utilities.Animations.ANIMATION_DURATION;
import static pranav.utilities.Animations.DI;

/**
 * Created on 27-08-2017 at 14:28 by Pranav Raut.
 * For QRCodeProtection
 */

public class DualView extends Listeners.l1<DualView> {

    private final View toView;
    private final View fromView;
    private final Context c;
    private final ViewGroup tParent;
    private Animations.AnimatingColor color = new Animations.AnimatingColor();

    private float sY;
    private float sX;
    private Utilities.Resources res;
    private float fH, fW, tH, tW;
    private Point t = new Point(), f = new Point();
    private long duration = ANIMATION_DURATION;
    private TimeInterpolator interpolator = DI;
    private boolean open;
    private Point oF = new Point(), oT = new Point();
    private View.OnClickListener l = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == fromView.getId()) openView();
        }
    };
    private ArrayList<State> states = new ArrayList<>();

    public DualView(ViewGroup parent, @IdRes int fromView, @IdRes int toView) {
        c = parent.getContext();
        this.tParent = parent;
        this.fromView = parent.findViewById(fromView);
        this.toView = parent.findViewById(toView);
        init();
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setInterpolator(TimeInterpolator interpolator) {
        this.interpolator = interpolator;
    }

    public void openView() {
        open = true;
        toView.setVisibility(View.VISIBLE);
        fromView.animate().x(t.x + tW / 2 - fW / 2).y(t.y + tH / 2 - fH / 2).scaleX(sX).scaleY(sY)
                .alpha(0).setDuration(duration).setInterpolator(interpolator).start();
        toView.animate().x(oT.x).y(oT.y).scaleX(1).scaleY(1)
                .alpha(1).setListener(getAnimator()).setInterpolator(interpolator)
                .setDuration(ANIMATION_DURATION).start();
    }

    public void dismiss() {
        open = false;
        fromView.animate().x(oF.x).y(oF.y).scaleX(1).scaleY(1)
                .alpha(1).setDuration(duration).setInterpolator(interpolator).start();
        toView.animate().x(f.x + fW / 2 - tW / 2).y(f.y + fH / 2 - tH / 2).scaleX(1 / sX).scaleY(1 / sY)
                .alpha(0).setDuration(duration).setInterpolator(interpolator).start();
    }

    private void init() {
        setChain(this);
        toView.setAlpha(0);
        fromView.setAlpha(1);
        fromView.setVisibility(View.VISIBLE);
        toView.setVisibility(View.VISIBLE);
        addListener(new L1.EndAnimation() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (open) {
                    for (State state : states)
                        if (state instanceof State.Open)
                            ((State.Open) state).onOpen();
                } else {
                    for (State state : states)
                        if (state instanceof State.Close)
                            ((State.Close) state).onClose();
                    toView.setVisibility(View.GONE);
                }
            }
        });
        tParent.addOnLayoutChangeListener(new View.OnLayoutChangeListener()

        {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                initVis();
            }
        });
    }

    public boolean isOpen() {
        return open;
    }

    private void initVis() {
        res = new Utilities.Resources(c);

        fromView.setOnClickListener(l);

        int[] ints = new int[2];

        toView.getLocationOnScreen(ints);
        t.x = ints[0];
        t.y = ints[1];
        fromView.getLocationOnScreen(ints = new int[2]);
        f.x = ints[0];
        f.y = (int) (ints[1] - res.getPx(24));

        oF.x = (int) fromView.getX();
        oF.y = (int) fromView.getY();
        oT.x = (int) toView.getX();
        oT.y = (int) toView.getY();

        toView.measure(WRAP_CONTENT, WRAP_CONTENT);
        fromView.measure(WRAP_CONTENT, WRAP_CONTENT);

        sX = (tW = toView.getMeasuredWidth()) / (fW = fromView.getMeasuredWidth());
        sY = (tH = toView.getMeasuredHeight()) / (fH = fromView.getMeasuredHeight());

        toView.setX(f.x + fW / 2f - tW / 2f);
        toView.setY(f.y + fH / 2f - tH / 2f);

        toView.setScaleX(1 / sX);
        toView.setScaleY(1 / sY);

        toView.setVisibility(View.GONE);

        color.setObjects(tParent);
    }

    @Override
    public DualView addListener(@NonNull L1... listeners) {
        for (L1 state : listeners)
            if (state instanceof State)
                states.add((State) state);
            else
                super.addListener(listeners);
        return this;
    }

    public interface State extends L1 {

        interface Open extends State {
            void onOpen();
        }

        interface Close extends State {
            void onClose();
        }

        interface Events extends Open, Close {
        }
    }
}