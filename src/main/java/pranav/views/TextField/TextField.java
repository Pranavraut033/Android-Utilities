package pranav.views.TextField;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.preons.pranav.utilities.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static pranav.utilities.DataBaseHelper.SQLiteQuery.TAG;


/**
 * Created on 02-08-2017 at 23:32 by Pranav Raut.
 * For QRCodeProtection
 */

@SuppressWarnings("unused")
public final class TextField<E extends EditText> extends FrameLayout {
    private static final String SAVED_STATE = "state";
    private static final String LIMIT = "limit";
    private static final String TEXT = "text";
    private static final String COUNT = "count";
    private static final String COLOR = "c";

    private final Context c;
    private TextInputLayout lt;
    private TextView ltv;
    private boolean ul;
    private int cr;

    private boolean full;
    private int count;
    private int limit;
    @NonNull
    private E textField;
    private int styleL = -1;
    private int styleT = -1;
    private String initHint;
    private int i;

    public TextField(Context c, E textField) {
        this(c, null, textField);
    }

    public TextField(Context c, AttributeSet a) {
        this(c, a, null);
    }

    @SuppressWarnings("unchecked")
    public TextField(Context c, @Nullable AttributeSet a, @Nullable E textField) {
        super(c, a);
        this.c = c;
        a(a);
        textField = textField == null ?
                (styleT != -1 ? (E) new TextInputEditText(c, a, styleT) :
                        (E) new TextInputEditText(c, a))
                : textField;
        init(textField, a);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (lt != null) lt.setVisibility(VISIBLE);
        textField.setVisibility(visibility);
    }

    public void init(E textField, AttributeSet attrs) {
        this.textField = textField;
        lt = styleL != -1 ? new TextInputLayout(this.c, attrs, styleL) :
                new TextInputLayout(this.c, attrs);
        addView(lt);
        lt.addView(textField);
        textField.setFocusable(true);
        textField.setFocusableInTouchMode(true);
        textField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                setLimitText(s.length());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setLimitText(s.length());
            }

            @Override
            public void afterTextChanged(Editable s) {
                setLimitText(s.length());
            }
        });
        textField.setOnFocusChangeListener((v, hasFocus) -> {
            if (ul) ltv.setTextColor(hasFocus ? 0x8a000000 : 0x61000000);
        });
        setUl(i > 0);
        setHint(initHint);
        setLimit(i);
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        if (direction == FOCUS_DOWN)
            return textField.requestFocus();
        return super.requestFocus(direction, previouslyFocusedRect);
    }

    private void a(AttributeSet a) {
        ltv = new TextView(new ContextThemeWrapper(c, R.style.text), null, R.style.text);
        addView(ltv);
        TypedArray typedArray = c.obtainStyledAttributes(a, R.styleable.TextField);
        initHint = typedArray.getString(R.styleable.TextField_hint);
        styleT = typedArray.getResourceId(R.styleable.TextField_styleText, -1);
        styleL = typedArray.getResourceId(R.styleable.TextField_styleLayout, -1);
        i = typedArray.getInteger(R.styleable.TextField_limit, -1);
        typedArray.recycle();
        if (ul) setLimitText(0);
    }

    public void setHint(@Nullable CharSequence hint) {
        if (lt != null) lt.setHint(hint);
        else textField.setHint(hint);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textField.setEnabled(enabled);
    }

    public void setError(String error) {
        textField.setError(error);
    }

    public void setLimit(int limit) {
        setUl((this.limit = limit) > 0);
        if (ul) {
            InputFilter[] filterArray = new InputFilter[1];
            filterArray[0] = new InputFilter.LengthFilter(limit);
            textField.setFilters(filterArray);
            setLimitText(0);
        }
    }

    private void setUl(boolean ul) {
        this.ul = ul;
        ltv.setVisibility(ul ? VISIBLE : GONE);
    }

    public void setLimitText(int ct) {
        if (full = ct > limit) return;
        ltv.setText((this.count = ct) + " / " + limit);
    }

    static void setDVal(ViewGroup.LayoutParams layoutParams) {
        layoutParams.height = WRAP_CONTENT;
        layoutParams.width = MATCH_PARENT;
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof EditText) {
            removeView(lt);
            init((E) child, null);
        } else {
            setDVal(params);
            super.addView(child, index, params);
        }
    }

    @Override
    @NonNull
    public Bundle onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SAVED_STATE, super.onSaveInstanceState());
        bundle.putString(TEXT, getContentText());
        bundle.putInt(LIMIT, limit);
        bundle.putInt(COUNT, count);
        bundle.putInt(COLOR, cr);
        return bundle;
    }

    @NonNull
    public String getContentText() {
        return textField.getText().toString();
    }

    public void onRestoreInstanceState(@Nullable Bundle state) {
        if (state != null) {
            super.onRestoreInstanceState(state.getParcelable(SAVED_STATE));
            textField.setText(state.getString(TEXT));
            ltv.setTextColor(state.getInt(COLOR));
            setLimit(state.getInt(LIMIT));
            setLimitText(state.getInt(COUNT));
        }
    }

    public String getText() {
        return textField.getText().toString();
    }

    public void setText(String s) {
        textField.setText(s);
    }

    public void setInputType(int type) {
        textField.setInputType(type);
    }

    @NonNull
    public E getTextField() {
        return textField;
    }

    public int getCount() {
        return count;
    }

    public boolean isFull() {
        return full;
    }

    public String getHint() {
        return (String) (lt != null ? lt.getHint() : textField.getHint());
    }

}