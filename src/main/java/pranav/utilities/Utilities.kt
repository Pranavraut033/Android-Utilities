@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package pranav.utilities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.Handler
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewTreeObserver
import android.view.Window
import android.widget.FrameLayout
import android.widget.PopupMenu
import androidx.annotation.*
import androidx.core.app.ActivityCompat
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pranav.views.DividerItemDecor
import java.io.*
import java.util.*
import kotlin.math.abs

object Utilities {
    private const val TAG = "Utilities"
    private val logger = Logger(TAG)
    /* Commonly used regex pattern */
    const val EMAIL_PATTERN =
        "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$"

    var MATCH: ViewGroup.LayoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)

    fun isFinite(f: Float): Boolean {
        return abs(f) <= java.lang.Float.MAX_VALUE
    }

    fun isFinite(d: Double): Boolean {
        return Math.abs(d) <= java.lang.Double.MAX_VALUE
    }

    fun isVisible(v: View): Boolean {
        return v.visibility == View.VISIBLE
    }

    fun titleCase(text: String?): String? {
        if (text == null || text.isEmpty()) {
            return text
        }

        val converted = StringBuilder()

        var convertNext = true
        for (ch in text.toCharArray()) {
            converted.append(
                when {
                    Character.isSpaceChar(ch) -> {
                        convertNext = true
                        ch
                    }
                    convertNext -> {
                        convertNext = false
                        Character.toTitleCase(ch)
                    }
                    else -> Character.toLowerCase(ch)
                }
            )
        }

        return converted.toString()
    }

    inline fun <reified T> getReverseArray(ts: Array<T>): Array<T> {
        var i = ts.size
        val ts1 = ArrayList<T>(i)
        for (t in ts) ts1.add(--i, t)
        return ts1.toTypedArray()
    }

    fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null)
            for (permission in permissions)
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                )
                    return false
        return true
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun setLightStatusBar(window: Window) {
        val view = window.decorView
        var flags = view.systemUiVisibility
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        view.systemUiVisibility = flags
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun clearLightStatusBar(window: Window) {
        val view = window.decorView
        var flags = view.systemUiVisibility
        flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        view.systemUiVisibility = flags
    }

    fun getSign(a: Double): Int {
        if (a == 0.0) return 0
        return if (a > 0)
            1
        else
            -1
    }

    fun checkAndAsk(
        activity: Activity,
        requestCode: Int,
        vararg permissions: String
    ): Boolean {
        val b: Boolean = hasPermissions(activity, *permissions)
        if (!b) ActivityCompat.requestPermissions(activity, permissions, requestCode)
        return b
    }

    fun addKeyboardToggleListener(
        a: Activity,
        listener: KeyboardToggleListener
    ): ViewTreeObserver.OnGlobalLayoutListener {
        val decorView = a.window.decorView.rootView
        val l = ViewTreeObserver.OnGlobalLayoutListener {
            val r = Rect()
            decorView.getWindowVisibleDisplayFrame(r)
            val screenHeight = decorView.height

            // r.bottom is the position above soft keypad or device button.
            // if keypad is shown, the r.bottom is smaller than that before.
            val keypadHeight = screenHeight - r.bottom
            // 0.15 ratio is perhaps enough to determine keypad height.
            if (keypadHeight > screenHeight * 0.15)
                listener.isKeyboardChange(true)
            else
                listener.isKeyboardChange(false)
        }
        decorView.viewTreeObserver.addOnGlobalLayoutListener(l)
        return l
    }

    /**
     * Function to compare multiple object
     *
     * @param object1 The object to compare
     * @param objects the collection of objects that the `object1` is being compared
     * @return false if anyone of the object in the collection doesn't matches to  `object1`
     */
    fun equalToCollection(object1: Any, vararg objects: Any): Boolean {
        for (o in objects) {
            if (object1 != o) {
                return false
            }
        }
        return true
    }

    /**
     * Function to compare multiple object
     *
     * @param object1 The object to compare
     * @param objects the collection of objects that the `object1` is being compared
     * @return `true` if anyone of the object in the collection matches to `object1`
     */
    fun isEqual_OR(object1: Any, vararg objects: Any): Boolean {
        for (o in objects) {
            if (object1 == o) {
                return true
            }
        }
        return false
    }

    /**
     * Function to compare multiple object
     *
     * @param object1 The object to compare
     * @param objects the collection of objects that the `object1` is being compared
     * @return `true` if anyone of the object in the collection matches to `object1`
     */
    fun isEqual_AND(object1: Any, vararg objects: Any): Boolean {
        for (o in objects) {
            if (object1 != o) {
                return false
            }
        }
        return true
    }

    /**
     * function to get first non-empty string
     *
     * @param strings all the alternate string to check before returning
     * @return first non empty string from `strings[]`
     */
    fun getValidString(vararg strings: String): String {
        for (s in strings)
            if (!TextUtils.isEmpty(s)) return s
        return "None"
    }

    class ResourceManager(private val context: Context) {
        private val metrics: DisplayMetrics = context.resources.displayMetrics

        val deviceHeight: Int
            get() = metrics.heightPixels

        val deviceWidth: Int
            get() = metrics.widthPixels

        fun getPx(dp: Float): Float {
            return dp * metrics.density
        }

        fun getPx(dp: Int): Int {
            return (dp * metrics.density).toInt()
        }

        fun getDrawable(@DrawableRes id: Int): Drawable? {
            @Suppress("DEPRECATION")
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                context.getDrawable(id)
            else context.resources.getDrawable(id)
        }

        fun decideOR(port: Int, land: Int): Int {
            return if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) land
            else port
        }

        fun getString(@StringRes id: Int): String {
            @Suppress("DEPRECATION")
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                context.getString(id)
            else context.resources.getString(id)
        }

        fun getColor(@ColorRes id: Int): Int {
            @Suppress("DEPRECATION")
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                context.getColor(id)
            else context.resources.getColor(id)
        }

        fun getDimen(@DimenRes id: Int): Float = context.resources.getDimension(id)

        fun getRawPixel(@DimenRes id: Int): Int = context.resources.getDimensionPixelSize(id)

        fun getRawPixel(@DimenRes id: Int, defaultValue: Int): Int =
            try {
                getRawPixel(id)
            } catch (e: Exception) {
                defaultValue
            }

        fun getDrawable(imageName: String): Drawable? {
            return getDrawable(
                context.resources.getIdentifier(
                    imageName,
                    "drawable",
                    context.packageName
                )
            )
        }

        fun oppositeX(x: Int): Int = deviceWidth - x

        fun oppositeY(y: Int): Int = deviceHeight - y

        fun oppositePoint(p: android.graphics.Point): android.graphics.Point =
            android.graphics.Point(oppositeX(p.x), oppositeY(p.y))

        companion object {

            fun getColoredDrawable(radius: Float, @ColorInt color: Int): Drawable {
                val drawable = GradientDrawable()
                drawable.shape = GradientDrawable.RECTANGLE
                drawable.setColor(color)
                drawable.cornerRadius = radius
                return drawable
            }

            fun getColoredDrawable(radius: Float, @ColorInt color: Int, padding: Int): Drawable {
                val drawable = GradientDrawable()
                drawable.shape = GradientDrawable.RECTANGLE
                drawable.setColor(color)
                drawable.setStroke(padding, 0x0)
                drawable.cornerRadius = radius
                return drawable
            }
        }
    }

    object Colors {
        fun blend(color1: Int, color2: Int): Int {
            return (Color.alpha(color1) + Color.alpha(color2)) / 2 shl 24 or
                    ((Color.red(color1) + Color.red(color2)) / 2 shl 16) or (
                    (Color.green(color1) + Color.green(color2)) / 2 shl 8) or
                    (Color.blue(color1) + Color.blue(color2)) / 2
        }

        @JvmOverloads
        fun blendA(color1: Int, color2: Int, ratio: Float = 1f): Int {
            return ColorUtils.blendARGB(color1, color2, ratio)
        }

        fun changeAlpha(color: Int, i: Float): Int {
            return (i * 0xff).toInt() shl 24 or (Color.red(color) shl 16) or (Color.green(color) shl 8) or Color.blue(
                color
            )
        }

        fun getBrightness(color: Int): Double {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)
            return Math.sqrt(
                red.toDouble() * red.toDouble() * .299 + green.toDouble() * green.toDouble() * .587 +
                        blue.toDouble() * blue.toDouble() * .114
            ) / 0xff
        }

        @JvmOverloads
        fun isBright(color: Int, threshold: Float = .5f): Boolean {
            return getBrightness(color) > threshold
        }

        @JvmOverloads
        fun toHex(str: String, toString: Boolean = true): String {
            var hash = 0
            var i = 0
            val color = StringBuilder(if (toString) "#" else "")
            if (str.isEmpty()) return "#000000"

            while (i < str.length) {
                hash = Character.codePointAt(str, 0) + ((hash shl 12) - hash)
                hash = hash and hash
                i++
            }

            i = 0
            while (i < 3) {
                val value = hash shr i * 8 and 255
                val t = "00" + Integer.toHexString(value)
                color.append(t.substring(t.length - 2))
                i++
            }
            return color.toString()
        }
    }

    abstract class DoubleClick @JvmOverloads constructor(private var duration: Long = 350) {

        private var o = true
        private var l: Long = 0
        private var d = false

        fun setDuration(duration: Long) {
            this.duration = duration
        }

        fun click() {
            if (o) {
                l = System.currentTimeMillis()
                o = false
                Handler().postDelayed({
                    o = true
                    if (!d) singleClickAction()
                }, duration)
            }
            val n = System.currentTimeMillis()
            if (n - l in 50..duration) {
                d = true
                doubleClickAction()
            } else
                d = false
        }

        protected abstract fun doubleClickAction()

        protected abstract fun singleClickAction()
    }

    fun initRec(decor: DividerItemDecor?, vararg recyclerViews: RecyclerView) {
        for (recyclerView in recyclerViews) {
            recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
            recyclerView.itemAnimator = DefaultItemAnimator()
            if (decor != null) recyclerView.addItemDecoration(decor)
        }
    }

    fun setForceShowIcon(popupMenu: PopupMenu) {
        try {
            val fields = popupMenu.javaClass.declaredFields
            for (field in fields) {
                if ("mPopup" == field.name) {
                    field.isAccessible = true
                    val menuPopupHelper = field.get(popupMenu)
                    val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                    val setForceIcons = classPopupHelper.getMethod(
                        "setForceShowIcon", Boolean::class.javaPrimitiveType!!
                    )
                    setForceIcons.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

    }

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    @SuppressLint("MissingPermission")
    fun isDeviceOnline(context: Context): Boolean {
        if (hasPermissions(context, Manifest.permission.ACCESS_NETWORK_STATE)) {
            val connMgr =
                context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            if (connMgr != null) {
                val networkInfo = connMgr.activeNetworkInfo
                return networkInfo != null && networkInfo.isConnected
            }
        }
        return false
    }

    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    abstract class QuickSort<E> {
        private var es: Array<E>? = null

        private fun partition(lo: Int, hi: Int): Int {
            var flo = lo
            var fhi = hi
            val p = flo++
            while (lo < hi) {
                while (getComparision(es!![lo], es!![p]) >= 0) flo++
                while (getComparision(es!![hi], es!![p]) < 0) fhi--
                if (lo < hi) swap(lo, hi)
            }
            swap(lo, hi)
            return hi
        }

        @JvmOverloads
        fun sort(es: Array<E>, lo: Int = 0, hi: Int = es.size - 1) {
            this.es = es
            this.qSort(es, lo, hi)
        }

        fun qSort(es: Array<E>, lo: Int, hi: Int) {
            if (lo < hi) {
                val p = partition(lo, hi)
                qSort(es, lo, p)
                qSort(es, p + 1, hi)
            }
        }

        private fun swap(a: Int, b: Int) {
            val temp = es!![b]
            es!![b] = es!![a]
            es!![a] = temp
        }

        /**
         * @param object0 Object 1
         * @param object1 Object 2
         * @return integer -<br></br>1) **Greater than 0** if `[object0][E]`
         * has **more weight** than `[object1][E]`<br></br>2) **Less
         * than 0** if `[object0][E]` has **less weight**
         * than `[object1][E]`<br></br>3) **0** if both Object are of
         * **equal** weight
         */
        protected abstract fun getComparision(object0: E, object1: E): Int
    }

    @Throws(IOException::class)
    fun copyFileUsingStream(source: File, dest: File) {
        FileInputStream(source).use { `is` ->
            FileOutputStream(dest).use { os ->
                val buffer = ByteArray(1024)
                while (true) {
                    val length: Int = `is`.read(buffer)
                    if (length > 0) os.write(buffer, 0, length)
                    else break
                }
            }
        }
    }

    fun fileToByteArray(f: File): ByteArray {
        val bytes = ByteArray(f.length().toInt())
        try {
            val fileInputStream = FileInputStream(f)
            if (fileInputStream.read(bytes) == 0) {
                logger.w("FileInputStream read 0 bytes")
            }
        } catch (e: FileNotFoundException) {
            println("File Not Found.")
            e.printStackTrace()
        } catch (e: IOException) {
            println("Error Reading The File.")
            e.printStackTrace()
        } catch (e: Exception) {
            println("Unknown Error")
            e.printStackTrace()
        }

        return bytes
    }

    interface KeyboardToggleListener {
        fun isKeyboardChange(isVisible: Boolean)
    }
}
