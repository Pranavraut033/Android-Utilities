package pranav.views.FloatingMenu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View.OnClickListener;
import android.view.Window;

import com.preons.pranav.utilities.R;

import java.util.Arrays;

import pranav.utilities.Utilities;

import static pranav.utilities.Utilities.Resources.getColoredDrawable;


/**
 * Created on 31-05-17 at 12:01 AM by Pranav Raut.
 * For QRCodeProtection
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public final class FMHelper {

    private int numOptions;
    private OnClickListener[] listeners;
    private Drawable mainBtnRes;
    private Drawable[] optionBtnRes;
    @ColorInt
    private int statusBarColor = 0x55000000;
    private Drawable imageBackground = new ColorDrawable(statusBarColor);
    @ColorInt
    private int mainBtnBaseColor;
    private int mainBtnOpenColor = 0xffffffff;
    @ColorInt
    private int[] optionBtnColors;
    private String optionTexts[];
    private Drawable optionTextBackground;
    private Utilities.Resources resources;
    private boolean isAnimatedStatusBar = true;
    private boolean isTintedFabSrc = true;
    private boolean isTintedStatusBarIcon = true;
    private boolean useCard = true;
    private Window window;

    public FMHelper(Context context) {
        this(3, context);
    }

    public FMHelper(int numOptions, Context context) {
        this.numOptions = numOptions;
        window = ((AppCompatActivity) context).getWindow();
        resources = new Utilities.Resources(context);
        optionBtnRes = new Drawable[numOptions];
        optionBtnColors = new int[numOptions];
        optionTexts = new String[numOptions];
        listeners = new OnClickListener[numOptions];
        mainBtnRes = resources.getDrawable(R.drawable.ic_add);
        Arrays.fill(optionBtnColors, mainBtnBaseColor = resources.getColor(R.color.colorPrimary));
        Arrays.fill(optionBtnRes, resources.getDrawable(R.drawable.ic_add));
        for (int i = 0; i < numOptions; i++) optionTexts[i] = "Option #" + (i + 1);
        this.optionTextBackground = getColoredDrawable(resources.getDimen(R.dimen.pad4dp), 0x90ffffff);
    }


    public int getNumOptions() {
        return numOptions;
    }

    public FMHelper setNumOptions(int numOptions) {
        this.numOptions = numOptions;
        return this;
    }

    public Drawable getMainBtnRes() {
        return mainBtnRes;
    }

    public FMHelper setMainBtnRes(Drawable mainBtnRes) {
        this.mainBtnRes = mainBtnRes;
        return this;
    }

    public FMHelper setMainBtnRes(@DrawableRes int mainBtnRes) {
        return setMainBtnRes(resources.getDrawable(mainBtnRes));
    }

    public OnClickListener[] getListeners() {
        return listeners;
    }

    public FMHelper setListeners(OnClickListener... listeners) {
        if (listeners.length < numOptions)
            throw new IllegalArgumentException("Length of listeners given is not equal to numOptions set in constructor");
        this.listeners = listeners;
        return this;
    }

    public Drawable[] getOptionBtnRes() {
        return optionBtnRes;
    }

    public FMHelper setOptionBtnRes(@DrawableRes int[] optionBtnRes) {
        if (optionBtnRes.length < numOptions)
            throw new IllegalArgumentException("Length of drawables given is not equal to numOptions set in constructor");
        for (int i = 0; i < numOptions; i++)
            this.optionBtnRes[i] = resources.getDrawable(optionBtnRes[i]);
        return this;
    }

    public FMHelper setOptionBtnRes(String[] resName) {
        if (resName.length < numOptions)
            throw new IllegalArgumentException("Length of drawables given is not equal to numOptions set in constructor ("
                    + numOptions + ", " + optionBtnColors.length + ")");
        for (int i = 0; i < numOptions; i++)
            this.optionBtnRes[i] = resources.getDrawable(resName[i]);
        return this;
    }

    public void setOptionBtnRes(Drawable[] optionBtnRes) {
        if (optionBtnRes.length < numOptions)
            throw new IllegalArgumentException("Length of drawables given is not equal to numOptions set in constructor");
        this.optionBtnRes = optionBtnRes;
    }

    public Drawable getImageBackground() {
        return imageBackground;
    }

    public FMHelper setImageBackground(Drawable imageBackground) {
        this.imageBackground = imageBackground;
        return this;
    }

    public FMHelper setImageBackground(@ColorInt int backColor) {
        this.imageBackground = new ColorDrawable(backColor);
        return this;
    }

    @ColorInt
    public int getMainBtnBaseColor() {
        return mainBtnBaseColor;
    }

    public FMHelper setMainBtnBaseColor(@ColorInt int color) {
        this.mainBtnBaseColor = color;
        return this;
    }

    public FMHelper setMainBtnBaseColor(String color) {
        return setMainBtnBaseColor(Color.parseColor(color));
    }

    @ColorInt
    public int getMainBtnOpenColor() {
        return mainBtnOpenColor;
    }

    public FMHelper setMainBtnOpenColor(@ColorInt int color) {
        this.mainBtnOpenColor = color;
        return this;
    }

    public FMHelper setMainBtnOpenColor(String color) {
        return setMainBtnOpenColor(Color.parseColor(color));
    }

    @ColorInt
    public int[] getOptionBtnColors() {
        return optionBtnColors;
    }

    public FMHelper setOptionBtnColors(@ColorInt int[] optionBtnColors) {
        if (optionBtnColors.length < numOptions)
            throw new IllegalArgumentException("Length of colors given is not equal to numOptions set in constructor ("
                    + numOptions + ", " + optionBtnColors.length + ")");
        this.optionBtnColors = optionBtnColors;
        return this;
    }

    public FMHelper setOptionBtnColors(@ColorInt int optionBtnColor) {
        Arrays.fill(optionBtnColors, optionBtnColor);
        return this;
    }

    public String[] getOptionTexts() {
        return optionTexts;
    }

    public FMHelper setOptionTexts(String[] optionTexts) {
        if (optionTexts.length < numOptions)
            throw new IllegalArgumentException("Length of texts given is not equal to numOptions set in constructor ("
                    + numOptions + ", " + optionTexts.length + ")");
        this.optionTexts = optionTexts;
        return this;
    }

    public FMHelper setOptionTexts(@StringRes int[] optionTexts) {
        if (optionTexts.length != numOptions)
            throw new IllegalArgumentException("Length of texts given is not equal to numOptions set in constructor ("
                    + numOptions + ", " + optionTexts.length + ")");
        for (int i = 0; i < optionTexts.length; i++)
            this.optionTexts[i] = resources.getString(optionTexts[i]);
        return this;
    }

    public Drawable getOptionTextBackground() {
        return optionTextBackground;
    }

    public FMHelper setOptionTextBackground(@DrawableRes int optionTextBackground) {
        return setOptionTextBackground(resources.getDrawable(optionTextBackground));
    }

    public FMHelper setOptionTextBackground(Drawable optionTextBackground) {
        this.optionTextBackground = optionTextBackground;
        return this;
    }

    public FMHelper setOptionTextBackgroundColor(@ColorInt int color) {
        return setOptionTextBackground(new ColorDrawable(color));
    }

    public int getStatusBarColor() {
        return statusBarColor;
    }

    public FMHelper setStatusBarColor(@ColorInt int statusBarColor) {
        this.statusBarColor = statusBarColor;
        return this;
    }

    public FMHelper setStatusBarColor(String color) {
        return setStatusBarColor(Color.parseColor(color));
    }

    public boolean isAnimatedStatusBar() {
        return isAnimatedStatusBar;
    }

    public FMHelper setAnimatedStatusBar(boolean animatedStatusBar) {
        this.isAnimatedStatusBar = animatedStatusBar;
        return this;
    }

    public boolean isTintedFabSrc() {
        return isTintedFabSrc;
    }

    public FMHelper setTintedFabSrc(boolean tintedFabSrc) {
        isTintedFabSrc = tintedFabSrc;
        return this;
    }

    @Nullable
    public Window getWindow() {
        return window;
    }

    public FMHelper setWindow(@NonNull Window window) {
        this.window = window;
        return this;
    }

    boolean getUseCard() {
        return useCard;
    }

    FMHelper setUseCard(boolean useCard) {
        this.useCard = useCard;
        return this;
    }
}