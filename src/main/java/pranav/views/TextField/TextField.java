package pranav.views.TextField;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.preons.pranav.utilities.R;

import java.util.HashMap;

import androidx.annotation.Nullable;
import pranav.utilities.Utilities;

@SuppressWarnings("unused")
public final class TextField extends FrameLayout {
    private static final String SAVED_STATE = "state";
    private static final String LIMIT = "limit";
    private static final String TEXT = "text";
    private static final String COUNT = "count";
    private static final String COLOR = "c";

    private final Context c;
    private final int[] avail_layout = {R.layout.outline,
            R.layout.filled,
            R.layout.outline_dense,
            R.layout.filled_dense};
    private final int[] avail_inputType = {
            InputType.TYPE_CLASS_TEXT,
            InputType.TYPE_CLASS_NUMBER,
            InputType.TYPE_NUMBER_VARIATION_PASSWORD,
            InputType.TYPE_CLASS_PHONE,
            InputType.TYPE_DATETIME_VARIATION_TIME,
            InputType.TYPE_DATETIME_VARIATION_DATE,
            InputType.TYPE_CLASS_DATETIME,
            InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS,
            InputType.TYPE_NUMBER_FLAG_DECIMAL,
            InputType.TYPE_NUMBER_VARIATION_PASSWORD,
            InputType.TYPE_NUMBER_FLAG_SIGNED,
            InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE,
            InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS,
            InputType.TYPE_TEXT_FLAG_CAP_WORDS,
            InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
            InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT,
            InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS,
            InputType.TYPE_TEXT_VARIATION_PASSWORD,
            InputType.TYPE_TEXT_VARIATION_PERSON_NAME,
            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD,
            InputType.TYPE_TEXT_FLAG_MULTI_LINE,
            InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT,
            InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS,
            InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD,
            -1,
    };

    private int cr;

    private TextInputLayout lt;
    private TextInputEditText textField;

    private boolean isRestricted;
    private boolean isFilled;
    private int count;
    private int limit;

    public TextField(Context c) {
        this(c, null);
    }

    @SuppressWarnings("unchecked")
    public TextField(Context c, @Nullable AttributeSet a) {
        super(c, a);
        this.c = c;
        init(a);
    }

    private void init(AttributeSet a) {
        Utilities.Resources res = new Utilities.Resources(c);
        HashMap<String, Object> attrs = a(a);

        lt = (TextInputLayout) LayoutInflater.from(c)
                .inflate(avail_layout[(int) attrs.get("styleType")],
                        this, false);
        textField = lt.findViewById(R.id.textField);
        addView(lt);

        textField.setHintTextColor(res.getColor(R.color.colorSecondary));
        lt.setBoxStrokeColor(res.getColor(R.color.colorSecondary));
        textField.setFocusable(true);
        textField.setFocusableInTouchMode(true);

        textField.setInputType(avail_inputType[(int) attrs.get("inputType")]);

        if ((boolean) attrs.get("singleLine")) {
            textField.setMaxLines(1);
            textField.setSingleLine();
        }

        int vertical = (int) attrs.get("fieldPaddingVertical"),
                horizontal = (int) attrs.get("fieldPaddingHorizontal"),
                start = (int) attrs.get("fieldPaddingStart"),
                end = (int) attrs.get("fieldPaddingEnd"),
                top = (int) attrs.get("fieldPaddingTop"),
                bottom = (int) attrs.get("fieldPaddingBottom");

        if (horizontal != 0) start = end = horizontal;
        if (vertical != 0) bottom = top = vertical;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            lt.setPaddingRelative(start, top, end, bottom);
        else lt.setPadding(start, top, end, bottom);

        setText((String) attrs.get("initialText"));
        setHelperText((CharSequence) attrs.get("helperText"));
        setHint((String) attrs.get("hint"));
        setLimit((int) attrs.get("limit"));
    }


    private HashMap<String, Object> a(AttributeSet a) {
        HashMap<String, Object> temp = new HashMap<>();

        TypedArray typedArray = c.obtainStyledAttributes(a, R.styleable.TextField);

        temp.put("limit", typedArray.getInteger(R.styleable.TextField_limit, -1));
        temp.put("styleType", typedArray.getInteger(R.styleable.TextField_textFieldStyle, 0));
        temp.put("inputType", typedArray.getInteger(R.styleable.TextField_inputType, 0));
        temp.put("singleLine", typedArray.getBoolean(R.styleable.TextField_singleLine, false));

        temp.put("hint", typedArray.getString(R.styleable.TextField_hint));
        temp.put("initialText", typedArray.getString(R.styleable.TextField_initialText));
        temp.put("fieldPaddingStart", typedArray.getDimension(R.styleable.TextField_helperText, 0));
        temp.put("fieldPaddingEnd", typedArray.getDimension(R.styleable.TextField_helperText, 0));
        temp.put("fieldPaddingTop", typedArray.getDimension(R.styleable.TextField_helperText, 0));
        temp.put("fieldPaddingBottom", typedArray.getDimension(R.styleable.TextField_helperText,
                0));
        temp.put("fieldPaddingHorizontal", typedArray.getDimension(R.styleable.TextField_helperText,
                0));
        temp.put("fieldPaddingVertical", typedArray.getDimension(R.styleable.TextField_helperText,
                0));

        typedArray.recycle();

        return temp;
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SAVED_STATE, super.onSaveInstanceState());
        bundle.putString(TEXT, getText());
        bundle.putInt(LIMIT, limit);
        bundle.putInt(COUNT, count);
        bundle.putInt(COLOR, cr);
        return bundle;
    }

    public void onRestoreInstanceState(@Nullable Bundle state) {
        if (state != null) {
            super.onRestoreInstanceState(state.getParcelable(SAVED_STATE));
            textField.setText(state.getString(TEXT));
            setLimit(state.getInt(LIMIT));
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        lt.setVisibility(VISIBLE);
        textField.setVisibility(visibility);
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        if (direction == FOCUS_DOWN)
            return textField.requestFocus();
        return super.requestFocus(direction, previouslyFocusedRect);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        lt.setEnabled(enabled);
    }

    public void setLimit(int limit) {
        setRestricted((this.limit = limit) > 0);
        if (isRestricted) {
            InputFilter[] filterArray = new InputFilter[1];
            filterArray[0] = new InputFilter.LengthFilter(limit);
            textField.setFilters(filterArray);
        }
    }

    public void setHint(@Nullable CharSequence hint) {
        lt.setHint(hint);
    }

    public void setError(String error) {
        lt.setError(error);
    }

    public void setHelperText(CharSequence helpText) {
        if (!TextUtils.isEmpty(helpText)) lt.setHelperText(helpText);
    }

    public void setRestricted(boolean restricted) {
        this.isRestricted = restricted;
        lt.setCounterEnabled(isRestricted);
    }

    public void setInputType(int type) {
        textField.setInputType(type);
    }

    public void setText(String s) {
        textField.setText(s);
    }

    public String getText() {
        return String.valueOf(textField.getText());
    }

    public TextInputEditText getTextField() {
        return textField;
    }

    public int getCount() {
        return count;
    }

    public boolean isFilled() {
        return isFilled;
    }

    public String getHint() {
        return String.valueOf(textField.getHint());
    }
}
