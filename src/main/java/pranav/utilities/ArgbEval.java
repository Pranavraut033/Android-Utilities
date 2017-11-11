package pranav.utilities;

import android.animation.ArgbEvaluator;
import android.animation.TimeInterpolator;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

/**
 * Created on 15-07-2017 at 09:55 by Pranav Raut.
 * For QRCodeProtection
 */
public class ArgbEval extends ArgbEvaluator {

    private final int startValue;
    private final int finalValue;
    private View object;
    private TimeInterpolator interpolator = new LinearInterpolator();

    public ArgbEval(int startValue, int finalValue) {
        this.startValue = startValue;
        this.finalValue = finalValue;
    }

    public Integer getValue(float fraction) {
        int value = (Integer) evaluate(interpolator.getInterpolation(fraction), startValue, finalValue);
        if (object != null) {
            if (object instanceof TextView)
                ((TextView) object).setTextColor(value);
            else
                object.setBackgroundColor(value);
        }
        return value;
    }

    public View getObject() {
        return object;
    }

    public ArgbEval setObject(View object) {
        this.object = object;
        return this;
    }

    public void setInterpolator(TimeInterpolator interpolator) {
        this.interpolator = interpolator;
    }
}
