package pranav.views.floatingMenu

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.preons.pranav.utilities.R
import pranav.utilities.Animations.ANIMATION_DURATION
import pranav.utilities.Animations.DI
import pranav.utilities.Utilities
import pranav.utilities.Utilities.Resources.getColoredDrawable

/**
 * Created on 09-06-2017 at 03:43 by Pranav Raut.
 * For QRCodeProtection
 */

class FMGroup : LinearLayout {
    private val useCard: Boolean
    private var params = LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    private lateinit var groupDetail: FMGroupHelper
    private val c = context
    var res = Utilities.Resources(c)
    private val container = LinearLayout(c)
    private val imageView = ImageView(c)
    private val textView = TextView(c)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        useCard = true
        inti()
    }

    constructor(context: Context, groupDetail: FMGroupHelper) : super(context) {
        this.groupDetail = groupDetail
        useCard = groupDetail.isUseCard
        inti()
    }

    private fun inti() {
        orientation = VERTICAL
        layoutParams = params
        container()
        textView()
        imageView()
    }

    private fun textView() {
        val s = groupDetail.headTitle
        if (s == null)
            textView.visibility = View.GONE
        else {
            textView.setPadding(0, 0, 0, res.getDimen(R.dimen.dPad).toInt())
            textView.layoutParams = params
            textView.text = s
            textView.setTextColor(if (useCard) -0x70000000 else -0x6f000001)
            val i = res.getDimen(R.dimen.dPad).toInt()
            if (useCard)
                textView.setPadding(i, textView.paddingTop, i, textView.paddingTop)
        }
    }

    private fun container() {
        container.layoutParams = params
        container.orientation = VERTICAL
    }

    private fun imageView() {
        if (!groupDetail.isDividerVisible)
            imageView.visibility = View.GONE
        else {
            val params1 = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                res.getDimen(R.dimen.pad2dp).toInt()
            )
            imageView.setImageDrawable(
                getColoredDrawable(
                    res.getDimen(R.dimen.pad2dp),
                    if (useCard) 0x7f000000 else 0x7fffffff
                )
            )
            imageView.layoutParams = params1
        }
    }

    fun addItem(item: FMItem) {
        container.addView(item)
    }

    fun add(): FMGroup {
        addView(textView)
        addView(container)
        addView(imageView)
        return this
    }

    fun initial() {
        textView.scaleX = 0f
        imageView.scaleX = 0f
    }

    fun animateOpen() {
        textView.animate().scaleX(1f).scaleY(1f).setDuration(ANIMATION_DURATION)
            .setStartDelay(100).interpolator = DI
        imageView.animate().scaleX(1f).setDuration(ANIMATION_DURATION)
            .setStartDelay(100).interpolator = DI
    }

    fun animateClose() {
        textView.animate().scaleY(0f).scaleX(0f).setDuration(ANIMATION_DURATION).interpolator = DI
        imageView.animate().scaleX(0f).setDuration(ANIMATION_DURATION).interpolator = DI
    }
}
