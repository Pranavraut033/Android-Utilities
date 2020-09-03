package pranav.views.floatingMenu

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View.OnClickListener
import android.view.Window
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.preons.pranav.utilities.R
import pranav.utilities.Utilities
import pranav.utilities.Utilities.ResourceManager.Companion.getColoredDrawable
import java.util.*


/**
 * Created on 31-05-17 at 12:01 AM by Pranav Raut.
 * For QRCodeProtection
 */

class FMHelper(var numOptions: Int, context: Context) {
    var listeners: Array<OnClickListener>? = null
    var mainBtnRes: Drawable? = null
    var optionBtnRes: Array<Drawable>? = null
    @ColorInt
    var statusBarColor = 0x55000000
    var imageBackground: Drawable = ColorDrawable(statusBarColor)
    @ColorInt
    var mainBtnBaseColor: Int = 0
    var mainBtnOpenColor = -0x1
    @ColorInt
    var optionBtnColors: Array<Int>? = null
    var optionTexts: Array<String>? = null
    var optionTextBackground: Drawable? = null
    private val resourceManager: Utilities.ResourceManager
    var isAnimatedStatusBar = true
    var isTintedFabSrc = true
    private val isTintedStatusBarIcon = true
    private var useCard = true
    var window: Window? = null

    constructor(context: Context) : this(3, context) {}

    init {
        window = (context as AppCompatActivity).window
        resourceManager = Utilities.ResourceManager(context)
        optionBtnRes = emptyArray()
        optionBtnColors = emptyArray()
        optionTexts = emptyArray()
        listeners = emptyArray()
        mainBtnRes = resourceManager.getDrawable(R.drawable.ic_add)
        mainBtnBaseColor = resourceManager.getColor(R.color.colorPrimary)

        Arrays.fill(optionBtnColors, mainBtnBaseColor)
        Arrays.fill(optionBtnRes, resourceManager.getDrawable(R.drawable.ic_add))
        for (i in 0 until numOptions) optionTexts!![i] = "Option #" + (i + 1)
        this.optionTextBackground =
            getColoredDrawable(resourceManager.getDimen(R.dimen.pad4dp), -0x6f000001)
    }


    fun setNumOptions(numOptions: Int): FMHelper {
        this.numOptions = numOptions
        return this
    }


    fun setMainBtnRes(mainBtnRes: Drawable?): FMHelper {
        this.mainBtnRes = mainBtnRes
        return this
    }

    fun setMainBtnRes(@DrawableRes mainBtnRes: Int): FMHelper {
        return setMainBtnRes(resourceManager.getDrawable(mainBtnRes))
    }


    fun setListeners(vararg listeners: OnClickListener): FMHelper {
        require(listeners.size >= numOptions) { "Length of listeners given is not equal to numOptions set in constructor" }
        this.listeners = arrayOf(*listeners)
        return this
    }

    fun setOptionBtnRes(@DrawableRes optionBtnRes: IntArray): FMHelper {
        require(optionBtnRes.size >= numOptions) { "Length of drawables given is not equal to numOptions set in constructor" }
        for (i in 0 until numOptions)
            this.optionBtnRes?.set(i, resourceManager.getDrawable(optionBtnRes[i])!!)
        return this
    }

    fun setOptionBtnRes(resName: Array<String>): FMHelper {
        require(resName.size >= numOptions) {
            ("Length of drawables given is not equal to numOptions set in constructor ("
                    + numOptions + ", " + optionBtnColors!!.size + ")")
        }
        for (i in 0 until numOptions)
            this.optionBtnRes?.set(i, resourceManager.getDrawable(resName[i])!!)
        return this
    }


    fun setImageBackground(imageBackground: Drawable): FMHelper {
        this.imageBackground = imageBackground
        return this
    }

    fun setImageBackground(@ColorInt backColor: Int): FMHelper {
        this.imageBackground = ColorDrawable(backColor)
        return this
    }


    fun setMainBtnBaseColor(@ColorInt color: Int): FMHelper {
        this.mainBtnBaseColor = color
        return this
    }

    fun setMainBtnBaseColor(color: String): FMHelper {
        return setMainBtnBaseColor(Color.parseColor(color))
    }


    fun setMainBtnOpenColor(@ColorInt color: Int): FMHelper {
        this.mainBtnOpenColor = color
        return this
    }

    fun setMainBtnOpenColor(color: String): FMHelper {
        return setMainBtnOpenColor(Color.parseColor(color))
    }


    fun setOptionBtnColors(@ColorInt optionBtnColors: Array<Int>): FMHelper {
        require(optionBtnColors.size >= numOptions) {
            ("Length of colors given is not equal to numOptions set in constructor ("
                    + numOptions + ", " + optionBtnColors.size + ")")
        }
        this.optionBtnColors = optionBtnColors
        return this
    }

    fun setOptionBtnColors(@ColorInt optionBtnColor: Int): FMHelper {
        Arrays.fill(optionBtnColors, optionBtnColor)
        return this
    }


    fun setOptionTexts(optionTexts: Array<String>): FMHelper {
        require(optionTexts.size >= numOptions) {
            ("Length of texts given is not equal to numOptions set in constructor ("
                    + numOptions + ", " + optionTexts.size + ")")
        }
        this.optionTexts = optionTexts
        return this
    }

    fun setOptionTexts(@StringRes optionTexts: IntArray): FMHelper {
        require(optionTexts.size == numOptions) {
            ("Length of texts given is not equal to numOptions set in constructor ("
                    + numOptions + ", " + optionTexts.size + ")")
        }
        for (i in optionTexts.indices)
            this.optionTexts?.set(i, resourceManager.getString(optionTexts[i]))
        return this
    }

    fun setOptionTextBackground(@DrawableRes optionTextBackground: Int): FMHelper {
        return setOptionTextBackground(resourceManager.getDrawable(optionTextBackground))
    }

    fun setOptionTextBackground(optionTextBackground: Drawable?): FMHelper {
        this.optionTextBackground = optionTextBackground
        return this
    }

    fun setOptionTextBackgroundColor(@ColorInt color: Int): FMHelper {
        return setOptionTextBackground(ColorDrawable(color))
    }


    fun setStatusBarColor(@ColorInt statusBarColor: Int): FMHelper {
        this.statusBarColor = statusBarColor
        return this
    }

    fun setStatusBarColor(color: String): FMHelper {
        return setStatusBarColor(Color.parseColor(color))
    }


    fun setAnimatedStatusBar(animatedStatusBar: Boolean): FMHelper {
        this.isAnimatedStatusBar = animatedStatusBar
        return this
    }


    fun setTintedFabSrc(tintedFabSrc: Boolean): FMHelper {
        isTintedFabSrc = tintedFabSrc
        return this
    }

    fun setWindow(window: Window): FMHelper {
        this.window = window
        return this
    }

    internal fun getUseCard(): Boolean {
        return useCard
    }

    internal fun setUseCard(useCard: Boolean): FMHelper {
        this.useCard = useCard
        return this
    }
}
