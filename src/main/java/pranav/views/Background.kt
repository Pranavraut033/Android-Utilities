package pranav.views

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.FloatRange
import androidx.annotation.RequiresApi
import pranav.utilities.Animations.ANIMATION_DURATION
import pranav.utilities.ArgbEval
import pranav.utilities.Utilities.MATCH
import java.lang.Double.isFinite

/**
 * Created on 10-07-2017 at 21:58 by Pranav Raut.
 * For QRCodeProtection
 */

class Background : FrameLayout {
    private var color = -0x70000000
    private var percent: Float = 0.toFloat()

    private var argbEval = ArgbEval(0, color)
    private var animator: ObjectAnimator? = null

    private var clickCaptureView: FrameLayout? = null
    private var keys: BackKeyListener? = null
    private var interpolator: TimeInterpolator? = null
    var l: Listeners.l1<Background>? = null
        private set
    private var state: Int = 0

    val isRunning: Boolean
        get() = l!!.isRunning

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        layoutParams = MATCH
        isFocusableInTouchMode = true
        isFocusable = true
        clickCaptureView = FrameLayout(context)
        clickCaptureView!!.layoutParams = MATCH
        super.addView(clickCaptureView)
        argbEval.setObject(this)
        clickCaptureView!!.id = BACK_ID
        l = Listeners.l1(this)
    }

    fun setClickListener(l: View.OnClickListener?): Background {
        clickCaptureView!!.setOnClickListener(l)
        return this
    }

    fun setColor(color: Int): Background {
        this.color = color
        argbEval = ArgbEval(0, color)
        return this
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) onBackPress(
            keyCode,
            event
        ) else super.onKeyDown(
            keyCode,
            event
        )
    }

    fun setKeys(onBackPress: BackKeyListener?): Background {
        this.keys = onBackPress
        return this
    }

    private fun onBackPress(keyCode: Int, event: KeyEvent): Boolean {
        val b = keys == null || keys!!.onBackPress(percent, keyCode, event)
        clearFocus()
        return b
    }

    fun animateTo(@FloatRange(from = 0.0, to = 100.0) percent: Float) {
        if (percent != this.percent)
            if (animator == null || !animator!!.isStarted) {
                animator = ObjectAnimator.ofFloat(this, "percent", 100 - percent, percent)
                animator!!.setDuration(ANIMATION_DURATION).interpolator = interpolator
                animator!!.addListener(l!!.animator)
                animator!!.start()
            }
    }

    internal fun addListener(vararg l1s: L1) {
        l!!.addListener(*l1s)
    }

    internal fun removeListener(vararg l1s: L1) {
        l!!.removeListener(*l1s)
    }

    fun setInterpolator(interpolator: TimeInterpolator) {
        this.interpolator = interpolator
    }

    fun toggle() {
        if (!isRunning) animateTo(100 - percent)
    }

    fun getPercent(): Float {
        return percent
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun setPercent(@FloatRange(from = 0.0, to = 100.0) percent: Float) {
        if (isFinite(percent.toDouble())) {
            if (percent == 100f)
                setState(View.VISIBLE)
            else if (percent == 0f) setState(View.GONE)
            argbEval.getValue(percent / 100f)
            this.percent = percent
        }
    }

    fun getState(): Int {
        return state
    }

    fun setState(@vi visibility: Int): Background {
        clickCaptureView!!.visibility = visibility
        animator = null
        if (visibility == View.VISIBLE) {
            percent = 100f
            requestFocus()
        } else
            percent = 0f
        return this
    }

    companion object {

        val BACK_ID = 2146
    }
}
