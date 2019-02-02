package pranav.views.customTextView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created on 23-01-2018 at 18:21 by Pranav Raut.
 * For Notes
 */

public class PatternEditView extends android.support.v7.widget.AppCompatEditText {
    private final AttributeSet attrs;
    private static final String TAG = "PatternEditView";

    ArrayList<Pattern> patterns = new ArrayList<>();

    public PatternEditView(Context context) {
        this(context, null);
    }

    public PatternEditView(Context context, AttributeSet attrs) {
        this(context, attrs, android.support.v7.appcompat.R.attr.editTextStyle);
    }

    public PatternEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.attrs = attrs;
        init();
    }

    private void init() {

    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        //super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    public void addPattern(String source, String replacement) {
        patterns.add(new Pattern(source, replacement));
    }

    private void log(String s, Object... os) {
        StringBuilder s1 = new StringBuilder();
        for (Object o : os) s1.append("\n").append(o.toString());
        Log.d(TAG, s + s1.toString());
    }

    private class Pattern {
        final String replacement;
        final String source;

        private Pattern(String replacement, String source) {
            this.replacement = replacement;
            this.source = source;
        }
    }
}
