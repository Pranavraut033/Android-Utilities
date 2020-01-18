package pranav.views.floatingMenu

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.transition.ArcMotion
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.preons.pranav.utilities.R
import pranav.utilities.Animations
import pranav.utilities.Animations.ANIMATION_DURATION
import pranav.utilities.Animations.animateAlpha
import pranav.utilities.Animations.animateRotate
import pranav.utilities.Log.TAG
import pranav.utilities.Utilities
import pranav.utilities.Utilities.getReverseArray
import java.util.*


/**
 * Created on 30-05-17 at 11:57 PM by Pranav Raut.
 * For QRCodeProtection
 */

class FloatingMenu @JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private val c: Float
    private val res: Utilities.Resources = Utilities.Resources(context)
    private var arcMotion: ArcMotion? = null
    private var drawable: Drawable? = null
    private var window: Window? = null
    private val menuItems = ArrayList<FMItem>()
    private val helpers = ArrayList<FMGroupHelper>()
    private val groups = ArrayList<FMGroup>()
    private var itemAnimation: ItemAnimation? = null
    private var statusBar: Animations.AnimateStatusBar? = null
    private val cvAnimator = Animations.AnimatingColor()
    private val mSRCAnimator = Animations.AnimatingColor()
    private val mbAnimator = Animations.AnimatingColor()
    private var mainContainer: LinearLayout? = null
    private var cardView: CardView? = null
    /**
     * @return recent used details
     */
    var details: FMHelper? = null
        private set
    private var background: ImageView? = null
    var mainBtn: FloatingActionButton? = null
        private set
    private val stateToSave: Int = 0

    private val listener = object : Animations.AnimatingColor.ColorChangeListener {
        override fun onColorChanged(color: Int) {
            mainBtn!!.backgroundTintList = ColorStateList(arrayOf(intArrayOf(0)), intArrayOf(color))
        }
    }
    private val listener1 = object : Animations.AnimatingColor.ColorChangeListener {
        override fun onColorChanged(color: Int) {
            drawable!!.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }
    private var oColor: Int = 0
    private var visible = false
    private var isMenuGroup = false
    var isUseCard = true
    private var i: Float = 0.toFloat()
    private var nXc: Float = 0.toFloat()
    private var nYc: Float = 0.toFloat()
    private var once = true
    private var oXc: Float = 0.toFloat()
    private var oYc: Float = 0.toFloat()
    private var nXb: Float = 0.toFloat()
    private var nYb: Float = 0.toFloat()
    private var oXb: Float = 0.toFloat()
    private var oYb: Float = 0.toFloat()
    private var v: Float = 0.toFloat()
    private var interpolator = Animations.DI
    val mainBtnListener = { _: View -> toggleMenu() }
    private val backgroundListener = { v: View ->
        visible = true
        mainBtnListener(v)
    }

    internal var events: Events? = null

    init {
        c = res.getDimen(R.dimen.pad28dp)
        inti()
        val childCount = childCount
        Log.i(TAG, "FloatingMenu: $childCount")
    }

    public override fun onSaveInstanceState(): Parcelable? {
        //begin boilerplate code that allows parent classes to save state
        val superState = super.onSaveInstanceState()
        val bundle = Bundle()
        bundle.putParcelable("superState", superState)
        return bundle
    }

    public override fun onRestoreInstanceState(_state: Parcelable?) {
        var state = _state
        if (state is Bundle) {
            val b = state as Bundle?
            state = b!!.getParcelable("superState")
        }
        super.onRestoreInstanceState(state)
    }

    private fun inti() {
        View.inflate(context, R.layout.floating_menu, this)
        mainContainer = findViewById(R.id.mainContainer)
        cardView = findViewById(R.id.menuCard)
        i = -res.getDimen(R.dimen.pad32dp)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            arcMotion = ArcMotion()
        }
        if (attrs != null)
            a()
        isFocusableInTouchMode = true
        if (isUseCard) cardView()
    }

    private fun a() {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FloatingMenu)
        val numOptions = typedArray.getInt(R.styleable.FloatingMenu_numOption, 3)
        val helper = FMHelper(numOptions, context)
        isUseCard = typedArray.getBoolean(R.styleable.FloatingMenu_useCard, true)
        val color =
            typedArray.getColor(R.styleable.FloatingMenu_baseOptionColor, helper.mainBtnBaseColor)
        val color1 =
            typedArray.getColor(R.styleable.FloatingMenu_statusBarColor, helper.statusBarColor)
        var drawable = typedArray.getDrawable(R.styleable.FloatingMenu_menuBackground)
        if (drawable == null) drawable = helper.imageBackground
        val ref = typedArray.getResourceId(R.styleable.FloatingMenu_optionTexts, 0)
        val ref2 = typedArray.getResourceId(R.styleable.FloatingMenu_optionIcons, 0)
        val strings = if (ref == 0) helper.optionTexts else context.resources.getStringArray(ref)
        if (ref2 != 0) helper.setOptionBtnRes(context.resources.getStringArray(ref2))
        val radius =
            typedArray.getDimension(R.styleable.FloatingMenu_cardRadius, res.getPx(2).toFloat())
        cardView!!.radius = radius
        helper.setUseCard(isUseCard)
            .setMainBtnBaseColor(color)
            .setImageBackground(drawable)
            .setStatusBarColor(color1).optionTexts = strings
        build(helper)
        typedArray.recycle()
    }

    fun build(helper: FMHelper) {
        mainContainer!!.removeAllViews()
        add(helper)
    }

    fun add(details: FMHelper) {
        this.details = details
        if (isUseCard)
            cardView!!.setCardBackgroundColor(details.mainBtnBaseColor)
        window = details.window

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) statusBar =
            Animations.AnimateStatusBar(window)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            oColor = window!!.statusBarColor
        menuItems()
        background()
        mainBtn()
    }

    fun build(vararg groupHelpers: FMGroupHelper) {
        mainContainer!!.removeAllViews()
        add(*groupHelpers)
    }

    fun add(vararg _groupDetails: FMGroupHelper) {
        var groupDetails = _groupDetails
        groupDetails = getReverseArray(groupDetails)
        isMenuGroup = true
        for (groupDetail in groupDetails) {
            groupDetail.isUseCard = isUseCard
            val group = FMGroup(context, groupDetail)
            helpers.add(groupDetail)
            groups.add(group)
            add(groupDetail.details)
            mainContainer!!.addView(group.add())
        }
    }

    private fun cardView() {
        cardView!!.cardElevation = res.getDimen(R.dimen.pad4dp)
        cardView!!.visibility = View.INVISIBLE
        mainContainer!!.setPadding(0, 0, 0, 0)
        cardView!!.translationX = i
        cardView!!.translationY = i
    }

    private fun menuItems() {
        for (i in 0 until details!!.numOptions) {
            val item = FMItem(context, isUseCard)
            item.setFabColor(details!!.optionBtnColors[i])
            item.setFabSrc(details!!.optionBtnRes[i])
            item.setOptionText(details!!.optionTexts[i])
            if (!isUseCard)
                item.setOptionBackground(details!!.optionTextBackground)
            item.setListener(details!!.listeners[i])
            menuItems.add(item)
            if (!isMenuGroup)
                mainContainer!!.addView(item)
            else
                groups[groups.size - 1].addItem(item)
            item.gravity = if (isUseCard) Gravity.START else Gravity.END
        }
        if (!isUseCard) {
            if (itemAnimation != null)
                itemAnimation!!.initialPosition(toArray())
            else
                for (i in menuItems.indices)
                    menuItems[i].initialPosition(menuItems.size - i)
            for (group in groups)
                group.initial()
        }
    }

    private fun background() {
        background = findViewById(R.id.floatingMenuBackground)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            background!!.background = details!!.imageBackground
        else
            background!!.setImageDrawable(details!!.imageBackground)
        background!!.setOnClickListener(backgroundListener)
    }

    private fun mainBtn() {
        drawable = details!!.mainBtnRes
        mainBtn = findViewById(R.id.mainFabBtn)
        mainBtn!!.setImageDrawable(details!!.mainBtnRes)
        mainBtn!!.backgroundTintList =
            ColorStateList(arrayOf(intArrayOf(0)), intArrayOf(details!!.mainBtnBaseColor))
        mainBtn!!.setOnClickListener(mainBtnListener)
        mbAnimator.setDuration(ANIMATION_DURATION)
        mbAnimator.setColorChangeListener(listener)
        mSRCAnimator.setDuration(ANIMATION_DURATION)
        mSRCAnimator.setColorChangeListener(listener1)
    }

    @Synchronized
    fun toggleMenu() {
        if (visible)
            hide()
        else
            show()
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun show() {
        background!!.setOnClickListener(backgroundListener)
        mainBtn!!.setOnClickListener(null)
        if (events != null) events!!.onOpen()
        requestFocus()
        visible = true
        animateAlpha(background!!, 1f)
        if (!isUseCard)
            mainBtnShow()
        statusBarShow()
        if (!isUseCard) {
            if (itemAnimation != null)
                itemAnimation!!.openAnimation(toArray())
            else {
                val items = getReverseArray(toArray())
                for (i in items.indices)
                    items[i].animateOpen(i * 25)
            }
            for (group in groups)
                group.animateOpen()
        } else {
            if (once) once()
            cardView!!.x = oXc
            cardView!!.y = oYc
            mainContainer!!.alpha = 0f
            cardView!!.visibility = View.VISIBLE
            mainContainer!!.translationY = oYb - nYb
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val animator = ObjectAnimator.ofFloat(
                    cardView, "x", "y",
                    arcMotion!!.getPath(oXc, oYc, nXc, nYc)
                )
                    .setDuration(ANIMATION_DURATION)
                val animator2 = ObjectAnimator.ofFloat(
                    mainBtn, "x", "y",
                    arcMotion!!.getPath(oXb, oYb, nXb, nYb)
                )
                    .setDuration(ANIMATION_DURATION)
                val anim = ViewAnimationUtils.createCircularReveal(
                    cardView, cardView!!.width / 2, cardView!!.height / 2, c, v
                )
                    .setDuration(ANIMATION_DURATION)
                animateAlpha(
                    mainBtn!!,
                    0f,
                    ANIMATION_DURATION - 50,
                    interpolator
                )
                animateAlpha(cardView!!, 1f, interpolator)
                anim.interpolator = interpolator
                anim.start()
                animator.interpolator = interpolator
                animator.start()
                animator2.interpolator = interpolator
                animator2.start()
            } else {
                cardView!!.scaleX = 0f
                cardView!!.scaleY = 0f
                cardView!!.animate().alpha(1f).scaleY(1f).scaleX(1f).x(nXc).y(nYc)
                    .setInterpolator(interpolator)
                    .setDuration(ANIMATION_DURATION).start()
                mainBtn!!.animate().alpha(0f).x(nXb).y(nYb).setInterpolator(interpolator)
                    .setDuration(ANIMATION_DURATION).start()
            }
            cvAnimator.setColors(details!!.mainBtnBaseColor, -0x1).start()
            mainContainer!!.animate().translationY(0f).setInterpolator(interpolator)
                .setDuration((ANIMATION_DURATION / 1.5).toLong())
                .setStartDelay(ANIMATION_DURATION / 4).start()
            animateAlpha(
                mainContainer!!,
                1f,
                (ANIMATION_DURATION * 1.2).toLong(),
                interpolator
            )
        }
    }

    private fun once() {
        nXc = cardView!!.x
        nYc = cardView!!.y
        oXc = mainBtn!!.x - cardView!!.width / 2 + mainBtn!!.width / 2
        oYc = mainBtn!!.y - cardView!!.height / 2 + mainBtn!!.width / 2
        oXb = mainBtn!!.x
        oYb = mainBtn!!.y
        nXb = cardView!!.x + cardView!!.width / 2 - mainBtn!!.height / 2
        nYb = cardView!!.y + cardView!!.height / 2 - mainBtn!!.height / 2
        v = Math.hypot(
            (cardView!!.width / 2).toDouble(),
            (cardView!!.height / 2).toDouble()
        ).toFloat()
        cvAnimator.setColorChangeListener(object : Animations.AnimatingColor.ColorChangeListener {
            override fun onColorChanged(color: Int) {
                cardView!!.setCardBackgroundColor(color)
            }
        })
        cvAnimator.setDuration(ANIMATION_DURATION)
        once = false
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN && visible) {
            toggleMenu()
            true
        } else
            super.onKeyDown(keyCode, event)
    }

    private fun statusBarShow() {
        if (details!!.isAnimatedStatusBar) {
            val t = details!!.statusBarColor
            val tC = blend(oColor, t)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                statusBar!!.animate(oColor, changeAlpha(tC, 1f))
            if (window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && isBright(if (Color.alpha(tC) != 0xff) blend(tC, -0x1) else tC, .8f)
            )
                window!!.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    private fun mainBtnShow() {
        animateRotate(mainBtn!!, 135f, DecelerateInterpolator())
        mbAnimator.setColors(details!!.mainBtnBaseColor, details!!.mainBtnOpenColor)
            .start()
        if (details!!.isTintedFabSrc) {
            val b = isBright(details!!.mainBtnOpenColor)
            mSRCAnimator.setColors(
                if (b) -0x44000001 else -0x45000000,
                if (b) -0x45000000 else -0x44000001
            ).start()
        }

        background!!.setOnClickListener(null)
        mainBtn!!.setOnClickListener(mainBtnListener)
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun hide() {
        background!!.setOnClickListener(null)
        mainBtn!!.setOnClickListener(mainBtnListener)
        if (events != null) events!!.onClose()
        clearFocus()
        visible = false
        animateAlpha(background!!, 0f)
        if (!isUseCard)
            mainBtnHide()
        if (details!!.isAnimatedStatusBar) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                statusBar!!.animate(
                    changeAlpha(blend(details!!.statusBarColor, oColor), 1f),
                    oColor
                )
        }
        if (!isUseCard) {
            if (itemAnimation != null)
                itemAnimation!!.closeAnimation(toArray())
            else
                for (i in menuItems.indices)
                    menuItems[i].animateClose(i * 25, menuItems.size - i)
            for (group in groups)
                group.animateClose()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cardView!!.visibility = View.VISIBLE
                val animator = ObjectAnimator.ofFloat(
                    cardView, "x", "y",
                    arcMotion!!.getPath(nXc, nYc, oXc, oYc)
                )
                    .setDuration(ANIMATION_DURATION)
                val animator2 = ObjectAnimator.ofFloat(
                    mainBtn, "x", "y",
                    arcMotion!!.getPath(nXb, nYb, oXb, oYb)
                )
                    .setDuration(ANIMATION_DURATION)
                val anim = ViewAnimationUtils.createCircularReveal(
                    cardView, cardView!!.width / 2, cardView!!.height / 2, v, c
                )
                    .setDuration(ANIMATION_DURATION)
                animateAlpha(
                    mainBtn!!,
                    1f,
                    ANIMATION_DURATION - 50,
                    interpolator
                )
                animateAlpha(cardView!!, 0f, interpolator)
                anim.interpolator = interpolator
                anim.start()
                animator.interpolator = interpolator
                animator.start()
                animator2.interpolator = interpolator
                animator2.start()
            } else {
                cardView!!.animate().alpha(0f).scaleY(0f).scaleX(0f).x(oXc).y(oYc)
                    .setInterpolator(interpolator)
                    .setDuration(ANIMATION_DURATION).start()
                mainBtn!!.animate().alpha(1f).x(oXb).y(oYb).setInterpolator(interpolator)
                    .setDuration(ANIMATION_DURATION).start()
            }
            cvAnimator.setColors(-0x1, details!!.mainBtnBaseColor).start()
            animateAlpha(mainContainer!!, 0f, interpolator)
        }
    }

    private fun mainBtnHide() {
        animateRotate(mainBtn!!, 0f, interpolator)
        if (details!!.isTintedFabSrc) {
            val b = isBright(details!!.mainBtnBaseColor)
            mSRCAnimator.setColors(
                if (b) -0x70000000 else -0x6f000001,
                if (b) -0x6f000001 else -0x70000000
            ).start()
        }
        mbAnimator.setColors(details!!.mainBtnOpenColor, details!!.mainBtnBaseColor).start()
    }

    private fun toArray(): Array<FMItem> {
        return menuItems.toTypedArray()
    }

    fun lift(height: Float) {
        liftFor(height, 0L)
    }

    fun liftFor(height: Float, duration: Long) {
        animate().translationY(-height).setDuration(ANIMATION_DURATION)
            .setInterpolator(interpolator).start()
        if (duration > 0)
            Handler().postDelayed({
                animate().translationY(0f).setDuration(ANIMATION_DURATION)
                    .setInterpolator(interpolator).start()
            }, duration)
    }

    fun setInterpolator(interpolator: Interpolator): FloatingMenu {
        this.interpolator = interpolator
        return this
    }

    fun setEvents(events: Events?) {
        this.events = events
    }

    interface Events {
        fun onOpen()

        fun onClose()
    }
}
