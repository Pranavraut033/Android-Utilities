package pranav.views.FloatingMenu;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.preons.pranav.utilities.R;

import pranav.utilities.Utilities;

import static pranav.utilities.Animations.ANIMATION_DURATION;
import static pranav.utilities.Animations.DI;
import static pranav.utilities.Utilities.Resources.getColoredDrawable;

/**
 * Created on 09-06-2017 at 03:43 by Pranav Raut.
 * For QRCodeProtection
 */

@SuppressWarnings("unused")
final class FMGroup extends LinearLayout {
    private final boolean useCard;
    LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    private FMGroupHelper groupDetail;
    private Context c = getContext();
    Utilities.Resources res = new Utilities.Resources(c);
    private LinearLayout container = new LinearLayout(c);
    private ImageView imageView = new ImageView(c);
    private TextView textView = new TextView(c);

    FMGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        useCard = true;
        inti();
    }

    FMGroup(Context context, FMGroupHelper groupDetail) {
        super(context);
        this.groupDetail = groupDetail;
        useCard = groupDetail.isUseCard();
        inti();
    }

    private void inti() {
        setOrientation(VERTICAL);
        setLayoutParams(params);
        container();
        textView();
        imageView();
    }

    private void textView() {
        String s = groupDetail.getHeadTitle();
        if (s == null) textView.setVisibility(GONE);
        else {
            textView.setPadding(0, 0, 0, (int) res.getDimen(R.dimen.dPad));
            textView.setLayoutParams(params);
            textView.setText(s);
            textView.setTextColor(useCard ? 0x90000000 : 0x90ffffff);
            int i = (int) res.getDimen(R.dimen.dPad);
            if (useCard)
                textView.setPadding(i, textView.getPaddingTop(), i, textView.getPaddingTop());
        }
    }

    private void container() {
        container.setLayoutParams(params);
        container.setOrientation(VERTICAL);
    }

    private void imageView() {
        if (!groupDetail.isDividerVisible()) imageView.setVisibility(GONE);
        else {
            LayoutParams params1 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) res.getDimen(R.dimen.pad2dp));
            imageView.setImageDrawable(getColoredDrawable(res.getDimen(R.dimen.pad2dp), useCard ? 0x7f000000 : 0x7fffffff));
            imageView.setLayoutParams(params1);
        }
    }

    void addItem(FMItem item) {
        container.addView(item);
    }

    FMGroup add() {
        addView(textView);
        addView(container);
        addView(imageView);
        return this;
    }

    void initial() {
        textView.setScaleX(0);
        imageView.setScaleX(0);
    }

    void animateOpen() {
        textView.animate().scaleX(1).scaleY(1).setDuration(ANIMATION_DURATION)
                .setStartDelay(100)
                .setInterpolator(DI);
        imageView.animate().scaleX(1).setDuration(ANIMATION_DURATION)
                .setStartDelay(100)
                .setInterpolator(DI);
    }

    void animateClose() {
        textView.animate().scaleY(0).scaleX(0).setDuration(ANIMATION_DURATION).setInterpolator(DI);
        imageView.animate().scaleX(0).setDuration(ANIMATION_DURATION).setInterpolator(DI);
    }
}
