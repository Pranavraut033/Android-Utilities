package pranav.utilities

import android.animation.*
import android.graphics.PorterDuff
import android.os.Build
import android.os.Handler
import android.view.View
import android.view.View.GONE
import android.view.Window
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Interpolator
import android.view.animation.Transformation
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.IntDef
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.viewpager.widget.ViewPager
import pranav.utilities.Utilities.isVisible
import pranav.views.L1
import pranav.views.Listeners
import java.util.*
import kotlin.math.abs


@Suppress("unused", "MemberVisibilityCanBePrivate")
object Animations {
    const val ANIMATION_DURATION: Long = 250
    const val USER_FRIENDLY_DELAY: Long = 2500
    val DI: Interpolator = AccelerateDecelerateInterpolator()

    fun toggleVisibility(view: View) {
        animateAlpha(view, (if (isVisible(view)) 0 else 1).toFloat())
    }

    @JvmOverloads
    fun animateAlpha(view: View, to: Float, interpolator: TimeInterpolator? = DI) {
        animateAlpha(view, to, ANIMATION_DURATION, interpolator)
    }

    fun animateAlpha(view: View, to: Float, animationTime: Long, interpolator: TimeInterpolator?) {
        animateAlpha(view, to, animationTime, interpolator, 0)
    }

    fun animateAlpha(
        view: View,
        to: Float,
        duration: Long,
        interpolator: TimeInterpolator?,
        delay: Long
    ) {
        if (!isVisible(view))
            view.visibility = View.VISIBLE
        view.alpha = abs(1 - to)
        view.animate().alpha(to).setDuration(ANIMATION_DURATION)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}

                override fun onAnimationEnd(animation: Animator) {
                    if (to == 0f) view.visibility = GONE
                }

                override fun onAnimationCancel(animation: Animator) {}

                override fun onAnimationRepeat(animation: Animator) {}
            }).setStartDelay(delay).setInterpolator(interpolator).duration = duration
    }

    @JvmOverloads
    fun animateScale(view: View, to: Float, interpolator: TimeInterpolator? = DI) {
        animateScale(view, to, ANIMATION_DURATION, interpolator)
    }

    fun animateScale(view: View, to: Float, animationTime: Long, interpolator: TimeInterpolator?) {
        animateScale(view, to, animationTime, interpolator, 0)
    }

    fun animateScale(
        view: View,
        to: Float,
        duration: Long,
        interpolator: TimeInterpolator?,
        delay: Long
    ) {
        if (to == view.alpha) return
        if (!isVisible(view))
            view.visibility = View.VISIBLE
        view.alpha = abs(1 - to)
        view.animate().scaleX(to).scaleY(to)
            .setDuration(ANIMATION_DURATION).setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}

                override fun onAnimationEnd(animation: Animator) {
                    if (to == 0f) view.visibility = GONE
                }

                override fun onAnimationCancel(animation: Animator) {}

                override fun onAnimationRepeat(animation: Animator) {}
            }).setStartDelay(delay).setInterpolator(interpolator).duration = duration
    }

    fun animateRotate(view: View, angle: Float, interpolator: Interpolator) {
        val valueAnimator = ObjectAnimator.ofFloat(view, "Rotation", 0f, angle)
        valueAnimator.duration = ANIMATION_DURATION
        valueAnimator.interpolator = interpolator
        valueAnimator.start()
    }


    class AnimatingDimensions(
        private val view: View,
        toFinal: Boolean,
        initialDimen: Float,
        targetDimen: Float
    ) : Listeners.l2<AnimatingDimensions>() {
        private val initialDimen: Float
        private val targetDimen: Float
        private var toFinal = true
        private var mode = ANIMATE_HEIGHT
        private var interpolator = DI
        private val running: Boolean = false

        private var duration: Long = -1
        private var delay: Long = 0

        constructor(view: View, initialDimen: Float, targetDimen: Float) : this(
            view,
            true,
            initialDimen,
            targetDimen
        )


        init {
            this.toFinal = toFinal
            this.initialDimen = if (initialDimen <= 0) 1f else initialDimen
            this.targetDimen = targetDimen.toInt().toFloat()
        }

        fun animate(show: Boolean) {
            this.toFinal = show
            animate()
        }

        fun animate() {
            val animation: Animation
            when (mode) {
                ANIMATE_HEIGHT -> view.layoutParams.height =
                    (if (toFinal) initialDimen else targetDimen).toInt()
                ANIMATE_WIDTH -> view.layoutParams.width =
                    (if (toFinal) initialDimen else targetDimen).toInt()
            }
            if (!isVisible(view)) view.visibility = View.VISIBLE
            if (toFinal) {
                animation = object : Animation() {
                    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                        val i =
                            (initialDimen + (targetDimen - initialDimen) * interpolatedTime).toInt()
                        when (mode) {
                            ANIMATE_HEIGHT ->
                                //                                if (interpolatedTime == 1)
                                //                                    view.getLayoutParams().height = WRAP_CONTENT;
                                //                                else
                                if (i >= 0) view.layoutParams.height = i
                            ANIMATE_WIDTH ->
                                //                                if (interpolatedTime == 1)
                                //                                    view.getLayoutParams().width = WRAP_CONTENT;
                                //                                else
                                if (i >= 0) view.layoutParams.width = i
                        }
                        view.requestLayout()
                    }

                    override fun willChangeBounds(): Boolean {
                        return true
                    }
                }
            } else {
                animation = object : Animation() {
                    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                        val i =
                            (targetDimen - (targetDimen - initialDimen) * interpolatedTime).toInt()
                        if (i >= 0) {
                            if (interpolatedTime == 0f) return
                            when (mode) {
                                ANIMATE_HEIGHT -> view.layoutParams.height = i
                                ANIMATE_WIDTH -> view.layoutParams.width = i
                            }
                            view.requestLayout()
                        }
                    }

                    override fun willChangeBounds(): Boolean {
                        return true
                    }
                }
            }
            animation.interpolator = interpolator
            animation.setAnimationListener(animationListener)
            animation.duration = if (duration == -1L)
                (targetDimen - initialDimen / view.context.resources.displayMetrics.density).toLong()
            else
                duration
            Handler().postDelayed({
                view.startAnimation(animation)
                toFinal = !toFinal
            }, delay)
        }

        fun setInterpolator(interpolator: Interpolator): AnimatingDimensions {
            this.interpolator = interpolator
            return this
        }

        fun setToFinal(toFinal: Boolean) {
            this.toFinal = toFinal
        }

        @AvailableOptions
        fun getMode(): Int {
            return mode
        }

        fun setMode(@AvailableOptions mode: Int): AnimatingDimensions {
            this.mode = mode
            return this
        }

        override fun isRunning(): Boolean {
            return running
        }

        fun setDuration(duration: Long): AnimatingDimensions {
            this.duration = duration
            return this
        }

        fun setDelay(delay: Long): AnimatingDimensions {
            this.delay = delay
            return this
        }

        fun toFinal(): Boolean {
            return toFinal
        }

        @IntDef(value = [ANIMATE_HEIGHT, ANIMATE_WIDTH])
        private annotation class AvailableOptions

        companion object {
            const val ANIMATE_HEIGHT = 0
            const val ANIMATE_WIDTH = 1

            fun animateHeight(v: View, top: Int, oldTop: Int, bottom: Int, oldBottom: Int) {
                val oldH = oldBottom - oldTop
                val newH = bottom - top
                animateHeight(v, oldH, newH)
            }

            fun animateHeight(v: View, _oldH: Int, _newH: Int) {
                var oldH = _oldH
                var newH = _newH
                val b = oldH > newH
                if (b) {
                    val t = oldH
                    oldH = newH
                    newH = t
                }
                AnimatingDimensions(v, oldH.toFloat(), newH.toFloat()).animate(!b)
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    class AnimateSystemBar(
        private val window: Window?,
        var mode: Int = ANIMATE_STATUS_BAR
    ) : Listeners.l1<AnimateSystemBar>() {
        companion object {
            const val ANIMATE_STATUS_BAR = 1
            const val ANIMATE_NAVIGATION_BAR = 2
            const val ANIMATE_BOTH = 3
        }

        private var interpolator: TimeInterpolator = DI
        private var duration = ANIMATION_DURATION
        var isLightStatusBar: Boolean = false
            private set

        val currentStatusColor: Int
            get() = window!!.statusBarColor

        init {
            setChain(this)
        }

        @Synchronized
        fun animate(colorFrom: Int, colorTo: Int) {
            animate(colorFrom, colorTo, false)
        }

        @Synchronized
        fun animate(_colorFrom: Int, _colorTo: Int, reverse: Boolean) {
            var colorFrom = _colorFrom
            var colorTo = _colorTo
            if (reverse) {
                colorFrom += colorTo
                colorTo = colorFrom - colorTo
                colorFrom -= colorTo
            }
            val animatingColor = AnimatingColor(colorFrom, colorTo)
            animatingColor.setColorChangeListener(object : AnimatingColor.ColorChangeListener {
                override fun onColorChanged(color: Int) {
                    if (window != null) {
                        when (mode) {
                            ANIMATE_NAVIGATION_BAR -> window.navigationBarColor = color
                            ANIMATE_STATUS_BAR -> window.statusBarColor = color
                            ANIMATE_BOTH -> {
                                window.navigationBarColor = color
                                window.statusBarColor = color
                            }
                        }
                    }
                }
            })
            animatingColor.addListener(listeners)
            animatingColor.setDuration(duration)
            animatingColor.setInterpolator(interpolator)
            animatingColor.start()
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Synchronized
        fun setLightStatusBar() {
            isLightStatusBar = true
            pranav.utilities.Utilities.setLightStatusBar(window!!)
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Synchronized
        fun clearLightStatusBar() {
            isLightStatusBar = false
            pranav.utilities.Utilities.clearLightStatusBar(window!!)
        }

        fun setInterpolator(interpolator: TimeInterpolator): AnimateSystemBar {
            this.interpolator = interpolator
            return this
        }

        fun setColor(@ColorInt color: Int) {
            window!!.statusBarColor = color
        }

        fun getDuration(): Long {
            return duration
        }

        fun setDuration(duration: Long): AnimateSystemBar {
            this.duration = duration
            return this
        }
    }

    class AnimatingColor : Listeners.l1<AnimatingColor>, Cloneable {

        var ended: Boolean = false
        private var colorTo: Int = 0
        private var colorFrom: Int = 0
        private var colorChangeListener: ColorChangeListener? = null
        private var objects = ArrayList<View>()
        private var duration = ANIMATION_DURATION
        private var delay: Long = 0
        private var interpolator: TimeInterpolator? = null
        /**
         * @return recent known animated Value
         */
        var value: Int = 0
            private set
        private val running = false
        private val listeners = arrayOfNulls<L1>(0)

        constructor(animatingColor: AnimatingColor) {
            setChain(this)
            setColors(animatingColor.colorTo, animatingColor.colorFrom)
                .setDelay(animatingColor.delay).setDuration(animatingColor.duration)
                .setColorChangeListener(animatingColor.colorChangeListener)
            objects = animatingColor.objects
        }

        @JvmOverloads
        constructor(colorFrom: Int = 0, colorTo: Int = 0) {
            setChain(this)
            value = colorFrom
            this.colorFrom = value
            this.colorTo = colorTo
        }

        constructor(colorTo: Int, vararg objects: View) {
            setChain(this)
            this.colorTo = colorTo
            setObjects(*objects)
        }

        fun animateTo() {
            for (v in objects) {
                var i = 0
                when (v) {
                    is TextView -> i = v.currentTextColor
                    is Toolbar -> for (j in 0 until v.childCount)
                        if (v.getChildAt(j) is TextView)
                            i = (v.getChildAt(j) as TextView).currentTextColor
                    else -> i = v.solidColor
                }
                setColors(i, colorTo).start()
            }
        }

        fun start(colorFrom: Int, colorTo: Int) {
            setColors(colorFrom, colorTo).start()
        }

        @JvmOverloads
        fun start(reverse: Boolean = false) {
            var colorFrom = this.colorFrom
            var colorTo = this.colorTo
            if (reverse) {
                colorFrom += colorTo
                colorTo = colorFrom - colorTo
                colorFrom -= colorTo
            }
            val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
            colorAnimation.addUpdateListener { animator ->
                value = animator.animatedValue as Int
                if (objects.isNotEmpty()) {
                    for (o in objects)
                        when (o) {
                            is ImageView -> o.setColorFilter(value, PorterDuff.Mode.SRC_ATOP)
                            is TextView -> o.setTextColor(value)
                            is CardView -> o.setCardBackgroundColor(value)
                            is Toolbar -> o.setTitleTextColor(value)
                            else -> o.setBackgroundColor(value)
                        }
                }
                if (colorChangeListener != null) colorChangeListener!!.onColorChanged(value)
            }
            colorAnimation.addListener(animator)
            colorAnimation.startDelay = delay
            colorAnimation.duration = duration
            colorAnimation.interpolator = interpolator
            colorAnimation.start()
        }

        fun setColors(colorFrom: Int, colorTo: Int): AnimatingColor {
            value = colorFrom
            this.colorFrom = value
            this.colorTo = colorTo
            return this
        }

        fun setColorChangeListener(colorChangeListener: ColorChangeListener?): AnimatingColor {
            this.colorChangeListener = colorChangeListener
            return this
        }

        fun getDuration(): Long {
            return duration
        }

        fun setDuration(duration: Long): AnimatingColor {
            this.duration = duration
            return this
        }

        fun setDelay(delay: Long): AnimatingColor {
            this.delay = delay
            return this
        }

        fun startDelayed(delay: Long) {
            this.delay = delay
            start()
        }

        fun setInterpolator(interpolator: TimeInterpolator): AnimatingColor {
            this.interpolator = interpolator
            return this
        }

        fun getObjects(): Array<View>? {
            return objects.toTypedArray()
        }

        fun removeObjects(vararg objects: View): AnimatingColor {
            if (objects.isNotEmpty())
                this.objects.removeAll(listOf(*objects))
            return this
        }

        fun setObjects(vararg objects: View): AnimatingColor {
            if (objects.isNotEmpty())
                this.objects.addAll(listOf(*objects))
            return this
        }

        interface ColorChangeListener {
            fun onColorChanged(color: Int)
        }
    }

    class Utilities {
        class ZoomOutPageTransformer : ViewPager.PageTransformer {

            override fun transformPage(view: View, position: Float) {
                val pageWidth = view.width
                val pageHeight = view.height

                when {
                    position < -1 -> // [-Infinity,-1)
                        // This page is way off-screen to the left.
                        view.alpha = 0f
                    position <= 1 -> { // [-1,1]
                        // Modify the default slide transition to shrink the page as well
                        val scaleFactor = MIN_SCALE.coerceAtLeast(1 - abs(position))
                        val vertMargin = pageHeight * (1 - scaleFactor) / 2
                        val horzMargin = pageWidth * (1 - scaleFactor) / 2
                        if (position < 0) {
                            view.translationX = horzMargin - vertMargin / 2
                        } else {
                            view.translationX = -horzMargin + vertMargin / 2
                        }

                        // Scale the page down (between MIN_SCALE and 1)
                        view.scaleX = scaleFactor
                        view.scaleY = scaleFactor

                        // Fade the page relative to its size.
                        view.alpha =
                            MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA)

                    }
                    else -> // (1,+Infinity]
                        // This page is way off-screen to the right.
                        view.alpha = 0f
                }
            }

            companion object {
                private const val MIN_SCALE = 0.85f
                private const val MIN_ALPHA = 0.5f
            }
        }

    }
}
