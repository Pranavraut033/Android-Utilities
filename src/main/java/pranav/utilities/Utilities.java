package pranav.utilities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.PopupMenu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import pranav.views.DividerItemDecor;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

@SuppressWarnings({"WeakerAccess", "unused", "SameParameterValue"})
public class Utilities {
    private static final String TAG = "Utilities";

    /* Commonly used regex pattern */
    public static final String EMAIL_PATTERN = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";

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

    public static int getSign(double a) {
        if (a == 0) return 0;
        if (a > 0) return 1;
        else return -1;
    }

    public static void checkAndAsk(AppCompatActivity activity, int requestCode, String... permissions) {
        if (!hasPermissions(activity, permissions))
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    public static ViewTreeObserver.OnGlobalLayoutListener addKeyboardToggleListener(AppCompatActivity a, KeyboardToggleListener listener) {
        View decorView = a.getWindow().getDecorView().getRootView();
        ViewTreeObserver.OnGlobalLayoutListener l;
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(l = () -> {
            Rect r = new Rect();
            decorView.getWindowVisibleDisplayFrame(r);
            int screenHeight = decorView.getHeight();

            // r.bottom is the position above soft keypad or device button.
            // if keypad is shown, the r.bottom is smaller than that before.
            int keypadHeight = screenHeight - r.bottom;
            // 0.15 ratio is perhaps enough to determine keypad height.
            if (keypadHeight > screenHeight * 0.15)
                listener.isKeyboardChange(true);
            else
                listener.isKeyboardChange(false);
        });
        return l;
    }

    /**
     * Function to compare multiple object
     *
     * @param object1 The object to compare
     * @param objects the collection of objects that the {@code object1} is being compared
     * @return false if anyone of the object in the collection doesn't matches to  {@code object1}
     */
    public static boolean isEqual_AND(Object object1, Object... objects) {
        for (Object o : objects) {
            if (!object1.equals(o)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Function to compare multiple object
     *
     * @param object1 The object to compare
     * @param objects the collection of objects that the {@code object1} is being compared
     * @return {@code true} if anyone of the object in the collection matches to {@code object1}
     */
    public static boolean isEqual_OR(Object object1, Object... objects) {
        for (Object o : objects) {
            if (object1.equals(o)) {
                return true;
            }
        }
        return false;
    }


    /**
     * function to get first non-empty string
     *
     * @param strings all the alternate string to check before returning
     * @return first non empty string from <code>strings[]</code>
     */
    @NonNull
    public static String getValidString(String... strings) {
        for (String s : strings)
            if (!TextUtils.isEmpty(s)) return s;
        return "None";
    }

    public static class Resources {

        private final Context context;
        private final DisplayMetrics metrics;

        public Resources(Context context) {
            if (context == null) throw new NullPointerException("Given Context is null");
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

        public int getPx(int dp) {
            return (int) (dp * metrics.density);
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

        public int getRawPixel(@DimenRes int id) {
            return context.getResources().getDimensionPixelSize(id);
        }

        public int getRawPixel(@DimenRes int id, int defaultValue) {
            try {
                return getRawPixel(id);
            } catch (Exception e) {
                return defaultValue;
            }
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

        public static int blendA(int color1, int color2) {
            return blendA(color1, color2, 1);
        }

        public static int blendA(int color1, int color2, float ratio) {
            return ColorUtils.blendARGB(color1, color2, ratio);
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

    public static void initRec(DividerItemDecor decor, RecyclerView... recyclerViews) {
        for (RecyclerView recyclerView : recyclerViews) {
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            if (decor != null)
                recyclerView.addItemDecoration(decor);
        }
    }

    public static void setForceShowIcon(PopupMenu popupMenu) {
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    @SuppressLint("MissingPermission")
    public static boolean isDeviceOnline(Context context) {
        if (hasPermissions(context, Manifest.permission.ACCESS_NETWORK_STATE)) {
            ConnectivityManager connMgr =
                    (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connMgr != null) {
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                return (networkInfo != null && networkInfo.isConnected());
            }
        }
        return false;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public abstract static class QuickSort<E> {
        private E[] es;

        private int partition(int lo, int hi) {
            int p = lo++;
            while (lo < hi) {
                while (getComparision(es[lo], es[p]) >= 0) lo++;
                while (getComparision(es[hi], es[p]) < 0) hi--;
                if (lo < hi) swap(lo, hi);
            }
            swap(lo, hi);
            return hi;
        }

        public final void sort(E[] es) {
            sort(es, 0, es.length - 1);
        }

        public final void sort(E[] es, int lo, int hi) {
            this.es = es;
            this.qSort(es, lo, hi);
        }

        public void qSort(E[] es, int lo, int hi) {
            if (lo < hi) {
                int p = partition(lo, hi);
                qSort(es, lo, p);
                qSort(es, p + 1, hi);
            }
        }

        private void swap(int a, int b) {
            E temp = es[b];
            es[b] = es[a];
            es[a] = temp;
        }

        /**
         * @param object0 Object 1
         * @param object1 Object 2
         * @return integer -<br>1) <strong>Greater than 0</strong> if <code>{@link E object0}</code>
         * has <strong>more weight</strong> than <code>{@link E object1}</code><br>2) <strong>Less
         * than 0</strong> if <code>{@link E object0}</code> has <strong>less weight</strong>
         * than <code>{@link E object1}</code><br>3) <strong>0</strong> if both Object are of
         * <strong>equal</strong> weight
         */
        protected abstract int getComparision(E object0, E object1);
    }

    public static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            if (is != null) is.close();
            if (os != null) os.close();
        }
    }

    public static byte[] fileToByteArray(File f) {
        byte[] bytes = new byte[(int) f.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(f);
            fileInputStream.read(bytes);
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error Reading The File.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Unknown Error");
            e.printStackTrace();
        }
        return bytes;
    }

    public interface KeyboardToggleListener {
        void isKeyboardChange(boolean isVisible);
    }
}