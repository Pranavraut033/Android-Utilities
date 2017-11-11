package pranav.utilities;

/**
 * Created on 30-08-2017 at 13:09 by Pranav Raut.
 * For QRCodeProtection
 */

public class Log {
    public static final String TAG = "Preons";
    private boolean loggingEnabled;

    public void d(String s) {
        if (loggingEnabled) android.util.Log.d("Preons", s);
    }

    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    public void setLoggingEnabled(boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
    }

    public void w(String s, Throwable throwable) {
        android.util.Log.w(TAG, s, throwable);
    }
}
