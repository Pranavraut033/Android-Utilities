package pranav.views.floatingMenu

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.preons.pranav.utilities.R
import pranav.utilities.Animations.ANIMATION_DURATION
import pranav.utilities.Animations.DI
import pranav.utilities.Utilities
import pranav.utilities.Utilities.Resources.getColoredDrawable

/**
 * Created on 31-05-17 at 06:53 PM by Pranav Raut.
 * For QRCodeProtection
 */

@Suppress("unused")
class FMItem : LinearLayout {

    private val res = Utilities.Resources(context)
    private var textView: TextView? = null
    private var actionButton: FloatingActionButton? = null
    var imageView: ImageView? = null
    private val d = ANIMATION_DURATION * 2 / 3
    private var useCard: Boolean = false

    internal constructor(context: Context, useCard: Boolean) : super(context) {
        this.useCard = useCard
        inti()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        inti()
    }

    private fun inti() {
        LayoutInflater.from(context).inflate(R.layout.floating_menu_item, this)
        textView = findViewById(R.id.optionText)
        actionButton = findViewById(R.id.optionFab)
        imageView = findViewById(R.id.menuIcon)
        if (useCard) {
            // TODO: 03-02-19 fix
            //            actionButton.setAlpha(GONE);
            actionButton!!.alpha = 0f
            val params = LayoutParams(textView!!.layoutParams)
            params.setMargins(0, 0, 0, 0)
            textView!!.layoutParams = params
            imageView!!.visibility = View.VISIBLE
            textView!!.setTextColor(-0x50000000)
            textView!!.textSize = 16f
        }

    }

    internal fun setOptionText(text: String) {
        textView!!.text = text
    }

    @Suppress("DEPRECATION")
    internal fun setOptionBackground(background: Drawable) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            textView!!.background = background
        else
            textView!!.setBackgroundDrawable(background)

    internal fun setFabSrc(src: Drawable) = if (useCard)
        imageView!!.setImageDrawable(src)
    else
        actionButton!!.setImageDrawable(src)

    @Suppress("DEPRECATION")
    internal fun setFabColor(@ColorInt color: Int) {
        val drawable = getColoredDrawable(res.getDimen(R.dimen.menuIcon), color)
        if (useCard)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                imageView!!.background = drawable
            else
                imageView!!.setBackgroundDrawable(drawable)
        else
            actionButton!!.backgroundTintList =
                ColorStateList(arrayOf(intArrayOf(0)), intArrayOf(color))
    }

    internal fun setListener(onClickListener: OnClickListener) {
        if (useCard)
            imageView!!.setOnClickListener(onClickListener)
        else
            actionButton!!.setOnClickListener(onClickListener)
        textView!!.setOnClickListener(onClickListener)

    }

    internal fun initialPosition(t: Int) {
        scaleX = 0f
        scaleY = 0f
        translationX = width * .4f
        translationY = (t * height).toFloat()
        rotation = 35f
        animate().interpolator = DecelerateInterpolator()
    }

    internal fun animateOpen(delay: Int) {
        animate().rotation(0f).scaleY(1f).scaleX(1f).translationX(0f)
            .setInterpolator(DI)
            .translationY(0f).setDuration(d).startDelay = delay.toLong()
    }

    internal fun animateClose(delay: Int, t: Int) {
        animate().rotation(35f).scaleY(0f).scaleX(0f).translationX(width * .4f)
            .setInterpolator(DI)
            .translationY((t * height).toFloat()).setDuration(d).startDelay = delay.toLong()
    }

}
