package pranav.views

import android.animation.TimeInterpolator
import android.content.Context
import android.graphics.Point
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.Interpolator
import androidx.annotation.IdRes
import pranav.utilities.Animations
import pranav.utilities.Animations.ANIMATION_DURATION
import pranav.utilities.Animations.DI
import pranav.utilities.Utilities
import java.util.*

/**
 * Created on 27-08-2017 at 14:28 by Pranav Raut.
 * For QRCodeProtection
 */

class DualView(private val tParent: ViewGroup, @IdRes fromView: Int, @IdRes toView: Int) :
    Listeners.l1<DualView>() {

    private val toView: View = tParent.findViewById(toView)
    private val fromView: View = tParent.findViewById(fromView)
    private val c: Context = tParent.context
    private val color = Animations.AnimatingColor()

    private var sY: Float = 0.toFloat()
    private var sX: Float = 0.toFloat()
    private var res: Utilities.ResourceManager? = null
    private var fH: Float = 0.toFloat()
    private var fW: Float = 0.toFloat()
    private var tH: Float = 0.toFloat()
    private var tW: Float = 0.toFloat()
    private val t = Point()
    private val f = Point()
    var duration = ANIMATION_DURATION
    private var interpolator = DI
    var isOpen: Boolean = false
        private set
    private val oF = Point()
    private val oT = Point()
    private val l = View.OnClickListener { v ->
        val i = v.id
        if (i == this.fromView.id) openView()
    }
    private val states = ArrayList<State>()

    init {
        init()
    }

    fun setInterpolator(interpolator: TimeInterpolator) {
        this.interpolator = interpolator as Interpolator
    }

    fun openView() {
        isOpen = true
        toView.visibility = View.VISIBLE
        fromView.animate().x(t.x + tW / 2 - fW / 2).y(t.y + tH / 2 - fH / 2).scaleX(sX).scaleY(sY)
            .alpha(0f).setDuration(duration).setInterpolator(interpolator).start()
        toView.animate().x(oT.x.toFloat()).y(oT.y.toFloat()).scaleX(1f).scaleY(1f)
            .alpha(1f).setListener(animator).setInterpolator(interpolator)
            .setDuration(ANIMATION_DURATION).start()
    }

    fun dismiss() {
        isOpen = false
        fromView.animate().x(oF.x.toFloat()).y(oF.y.toFloat()).scaleX(1f).scaleY(1f)
            .alpha(1f).setDuration(duration).setInterpolator(interpolator).start()
        toView.animate().x(f.x + fW / 2 - tW / 2).y(f.y + fH / 2 - tH / 2).scaleX(1 / sX)
            .scaleY(1 / sY)
            .alpha(0f).setDuration(duration).setInterpolator(interpolator).start()
    }

    private fun init() {
        setChain(this)
        toView.alpha = 0f
        fromView.alpha = 1f
        fromView.visibility = View.VISIBLE
        toView.visibility = View.VISIBLE
        addListener(L1.EndAnimation {
            if (isOpen) {
                for (state in states)
                    if (state is State.Open)
                        state.onOpen()
            } else {
                for (state in states)
                    if (state is State.Close)
                        state.onClose()
                toView.visibility = View.GONE
            }
        })
        tParent.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View, left: Int, top: Int, right: Int, bottom: Int,
                oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int
            ) {
                v.removeOnLayoutChangeListener(this)
                initVis()
            }
        })
    }

    private fun initVis() {
        res = Utilities.ResourceManager(c)

        fromView.setOnClickListener(l)

        val ints = IntArray(2)

        toView.getLocationOnScreen(ints)
        t.x = ints[0]
        t.y = ints[1]
        fromView.getLocationOnScreen(IntArray(2))
        f.x = ints[0]
        f.y = ints[1] - res!!.getPx(24)

        oF.x = fromView.x.toInt()
        oF.y = fromView.y.toInt()
        oT.x = toView.x.toInt()
        oT.y = toView.y.toInt()

        toView.measure(WRAP_CONTENT, WRAP_CONTENT)
        fromView.measure(WRAP_CONTENT, WRAP_CONTENT)
        tW = toView.measuredWidth.toFloat()
        fW = fromView.measuredWidth.toFloat()
        tH = toView.measuredHeight.toFloat()
        fH = fromView.measuredHeight.toFloat()
        sX = tW / fW
        sY = tH / fH

        toView.x = f.x + fW / 2f - tW / 2f
        toView.y = f.y + fH / 2f - tH / 2f

        toView.scaleX = 1 / sX
        toView.scaleY = 1 / sY

        toView.visibility = View.GONE

        color.setObjects(tParent)
    }

    override fun addListener(vararg listeners: L1): DualView {
        for (state in listeners)
            if (state is State)
                states.add(state)
            else
                super.addListener(*listeners)
        return this
    }

    interface State : L1 {

        interface Open : State {
            fun onOpen()
        }

        interface Close : State {
            fun onClose()
        }

        interface Events : Open, Close
    }
}
