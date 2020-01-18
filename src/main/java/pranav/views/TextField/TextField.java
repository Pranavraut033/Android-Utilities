package pranav.views.TextField;

import android.content.Context;
import android.content.res.ColorStateList;
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

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.preons.pranav.utilities.R;

import java.util.HashMap;
import java.util.Objects;

import pranav.utilities.Utilities;


@SuppressWarnings("unused")
public final class TextField extends FrameLayout {
    private static final String TAG = "TextField";

    private static final String SAVED_STATE = "state";
    private static final String LIMIT = "limit";
    private static final String TEXT = "text";
    private static final String COUNT = "count";
    private static final String COLOR = "c";
    private static final String COUNTER_ENABLED = "c_enable";

    private final Context c;
    private final int[] avail_layout = {
            R.layout.outline,
            R.layout.filled,
            R.layout.outline_dense,
            R.layout.filled_dense
    };
    private final int[] avail_inputType = {
            InputType.TYPE_CLASS_TEXT,
            InputType.TYPE_CLASS_NUMBER,
            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD,
            InputType.TYPE_CLASS_PHONE,
            InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME,
            InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE,
            InputType.TYPE_CLASS_DATETIME,
            InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL,
            InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD,
            InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED,
            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS,
            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE,
            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS,
            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS,
            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT,
            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS,
            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD,
            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME,
            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD,
            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE,
            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT,
            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS,
            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD,
            -1,
    };

    private int cr;

    private TextInputLayout lt;
    private TextInputEditText textField;

    private boolean isFilled;
    private int count;
    private int limit;
    private Boolean counterEnabled;

    public TextField(Context c) {
        this(c, null);
    }

    public TextField(Context c, @Nullable AttributeSet a) {
        super(c, a);
        this.c = c;
        init(a);
    }

    private void init(AttributeSet a) {
        Utilities.Resources res = new Utilities.Resources(c);
        HashMap<String, Object> attrs = a(a);
        int textFieldColor = (int) attrs.get("textFieldColor");

        lt = (TextInputLayout) LayoutInflater.from(c)
                .inflate(avail_layout[(int) attrs.get("styleType")],
                        this, false);
        textField = lt.findViewById(R.id.textField);

        textField.setFocusable(true);
        textField.setFocusableInTouchMode(true);

        textField.setInputType(avail_inputType[(int) attrs.get("inputType")]);
        textField.setMinLines((int) attrs.get("minLines"));
        textField.setLines((int) attrs.get("minLines"));


        if (textFieldColor != 0) {
            int t = Objects.requireNonNull(lt.getHintTextColor()).getDefaultColor();

            ColorStateList hintTextColors = new ColorStateList(
                    new int[][]{
                            new int[]{android.R.attr.state_active},
                            new int[]{android.R.attr.state_focused},
                            new int[]{-android.R.attr.state_active},
                            new int[]{-android.R.attr.state_focused}
                    },
                    new int[]{
                            textFieldColor,
                            textFieldColor,
                            t,
                            t
                    }
            );

            lt.setBoxStrokeColor(textFieldColor);
            lt.setHintTextColor(hintTextColors);
            textField.setHintTextColor(textFieldColor);
        }

        if ((boolean) attrs.get("singleLine")) {
            textField.setMaxLines(1);
            textField.setSingleLine();
        }

        int vertical = Math.round((float) attrs.get("fieldPaddingVertical")),
                horizontal = Math.round((float) attrs.get("fieldPaddingHorizontal")),
                start = Math.round((float) attrs.get("fieldPaddingStart")),
                end = Math.round((float) attrs.get("fieldPaddingEnd")),
                top = Math.round((float) attrs.get("fieldPaddingTop")),
                bottom = Math.round((float) attrs.get("fieldPaddingBottom"));

        if (horizontal != 0) start = end = horizontal;
        if (vertical != 0) bottom = top = vertical;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            lt.setPaddingRelative(start, top, end, bottom);
        else lt.setPadding(start, top, end, bottom);

        addView(lt);

        setText((String) attrs.get("initialText"));
        setHelperText((CharSequence) attrs.get("helperText"));
        setHint((String) attrs.get("hint"));
        this.counterEnabled = (Boolean) attrs.get("showCounter");
        setLimit((int) attrs.get("limit"), this.counterEnabled);
    }

    private HashMap<String, Object> a(AttributeSet a) {
        HashMap<String, Object> temp = new HashMap<>();

        TypedArray typedArray = c.obtainStyledAttributes(a, R.styleable.TextField);

        temp.put("limit", typedArray.getInteger(R.styleable.TextField_limit, -1));
        temp.put("showCounter", typedArray.getBoolean(R.styleable.TextField_showCounter, true));
        temp.put("styleType", typedArray.getInteger(R.styleable.TextField_textFieldStyle, 0));
        temp.put("inputType", typedArray.getInteger(R.styleable.TextField_inputType, 0));
        temp.put("singleLine", typedArray.getBoolean(R.styleable.TextField_singleLine, false));

        temp.put("hint", typedArray.getString(R.styleable.TextField_hint));
        temp.put("initialText", typedArray.getString(R.styleable.TextField_initialText));
        temp.put("minLines", typedArray.getInteger(R.styleable.TextField_minLines, 1));

        temp.put("fieldPaddingStart", typedArray.getDimension(R.styleable.TextField_fieldPaddingStart,
                0));
        temp.put("fieldPaddingEnd", typedArray.getDimension(R.styleable.TextField_fieldPaddingEnd,
                0));
        temp.put("fieldPaddingTop", typedArray.getDimension(R.styleable.TextField_fieldPaddingTop,
                0));
        temp.put("fieldPaddingBottom", typedArray.getDimension(R.styleable.TextField_fieldPaddingBottom,
                0));
        temp.put("fieldPaddingHorizontal", typedArray.getDimension(R.styleable.TextField_fieldPaddingHorizontal,
                0));
        temp.put("fieldPaddingVertical", typedArray.getDimension(R.styleable.TextField_fieldPaddingVertical,
                0));
        temp.put("textFieldColor", typedArray.getColor(R.styleable.TextField_textFieldColor, 0));
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
        bundle.putBoolean(COUNTER_ENABLED, this.counterEnabled);
        return bundle;
    }

    public void onRestoreInstanceState(@Nullable Bundle state) {
        if (state != null) {
            super.onRestoreInstanceState(state.getParcelable(SAVED_STATE));
            textField.setText(state.getString(TEXT));
            setLimit(state.getInt(LIMIT), state.getBoolean(COUNTER_ENABLED));
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

    public void setLimit(int limit, boolean toShow) {
        boolean restricted = (this.limit = limit) > 0;
        setCounterEnabled(toShow && restricted);

        if (restricted) {
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

    public void setCounterEnabled(boolean restricted) {
        lt.setCounterEnabled(restricted);
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

    public boolean setErrorIfEmpty(String error) {
        final boolean b = TextUtils.isEmpty(getText());
        if (b) {
            setError(error);
            requestFocus();
        }
        return b;
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
