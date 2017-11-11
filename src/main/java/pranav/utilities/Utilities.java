package pranav.utilities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import java.util.ArrayList;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class Utilities {

    public static ViewGroup.LayoutParams MATCH =
            new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);

    public static boolean isFinite(float f) {
        return Math.abs(f) <= Float.MAX_VALUE;
    }

    public static boolean isFinite(double d) {
        return Math.abs(d) <= Double.MAX_VALUE;
    }

    public static boolean isVisible(View v) {
        return v.getVisibility() == View.VISIBLE;
    }

    @NonNull
    public static <T> T[] getReverseArray(T[] ts) {
        int i = ts.length;
        ArrayList<T> ts1 = new ArrayList<>(i);
        for (T t : ts) ts1.add(--i, t);
        return ts1.toArray(ts);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null)
            for (String permission : permissions)
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                    return false;
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void setLightStatusBar(Window window) {
        View view = window.getDecorView();
        int flags = view.getSystemUiVisibility();
        flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        view.setSystemUiVisibility(flags);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void clearLightStatusBar(Window window) {
        View view = window.getDecorView();
        int flags = view.getSystemUiVisibility();
        flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        view.setSystemUiVisibility(flags);
    }

    public static class Resources {

        private final Context context;
        private final DisplayMetrics metrics;

        public Resources(Context context) {
            this.context = context;
            metrics = context.getResources().getDisplayMetrics();
        }

        public static Drawable getColoredDrawable(float radius, @ColorInt int color) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setColor(color);
            drawable.setCornerRadius(radius);
            return drawable;
        }

        public static Drawable getColoredDrawable(float radius, @ColorInt int color, int padding) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setColor(color);
            drawable.setStroke(padding, 0x0);
            drawable.setCornerRadius(radius);
            return drawable;
        }

        public int getDeviceHeight() {
            return metrics.heightPixels;
        }

        public int getDeviceWidth() {
            return metrics.widthPixels;
        }

        public float getPx(float dp) {
            return dp * metrics.density;
        }

        public Drawable getDrawable(@DrawableRes int id) {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ?
                    context.getDrawable(id) : context.getResources().getDrawable(id);
        }

        public int decideOR(int port, int land) {
            return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ?
                    land : port;
        }

        public String getString(@StringRes int id) {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ?
                    context.getString(id) : context.getResources().getString(id);
        }

        public int getColor(@ColorRes int id) {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                    context.getColor(id) : context.getResources().getColor(id);
        }

        public float getDimen(@DimenRes int id) {
            return context.getResources().getDimension(id);
        }

        public Drawable getDrawable(String imageName) {
            return getDrawable(context.getResources().getIdentifier(imageName, "drawable", context.getPackageName()));
        }

        public int oppositeX(int x) {
            return getDeviceWidth() - x;
        }

        public int oppositeY(int y) {
            return getDeviceHeight() - y;
        }

        public android.graphics.Point oppositePoint(android.graphics.Point p) {
            return new android.graphics.Point(oppositeX(p.x), oppositeY(p.y));
        }
    }

    public static class Colors {

        public static int blend(int color1, int color2) {
            return ((Color.alpha(color1) + Color.alpha(color2)) / 2) << 24 |
                    ((Color.red(color1) + Color.red(color2)) / 2 << 16) |
                    ((Color.green(color1) + Color.green(color2)) / 2) << 8 |
                    (Color.blue(color1) + Color.blue(color2)) / 2;
        }

        public static int changeAlpha(int color, float i) {
            return ((int) (i * 0xff) << 24) | (Color.red(color) << 16) | (Color.green(color) << 8) | Color.blue(color);
        }

        public static double getBrightness(int color) {
            int red = Color.red(color);
            int green = Color.green(color);
            int blue = Color.blue(color);
            return Math.sqrt(red * red * .299 + green * green * .587 +
                    blue * blue * .114) / 0xff;
        }

        public static boolean isBright(int color) {
            return isBright(color, .5f);
        }

        public static boolean isBright(int color, float threshold) {
            return getBrightness(color) > threshold;
        }
    }

    public static abstract class DoubleClick {

        private boolean o = true;
        private long l;
        private long duration;
        private boolean d = false;

        public DoubleClick() {
            this(350);
        }

        public DoubleClick(long duration) {
            this.duration = duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public void click() {
            if (o) {
                l = System.currentTimeMillis();
                o = false;
                new Handler().postDelayed(() -> {
                    o = true;
                    if (!d) singleClickAction();
                }, duration);
            }
            long n = System.currentTimeMillis();
            if (n - l >= 50 && n - l <= duration) {
                d = true;
                doubleClickAction();
            } else
                d = false;
        }

        protected abstract void doubleClickAction();

        protected abstract void singleClickAction();
    }
}
