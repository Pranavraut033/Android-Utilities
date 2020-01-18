package pranav.views

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.IntRange
import androidx.appcompat.app.AppCompatActivity
import com.preons.pranav.utilities.R
import pranav.utilities.Animations.ANIMATION_DURATION
import pranav.utilities.PopMenu

/**
 * Created on 28-02-2018 at 00:47 by Pranav Raut.
 * For Notes
 */

class MiniToolBar @JvmOverloads constructor(
    private val mContext: Context,
    attrs: AttributeSet? = null
) : FrameLayout(mContext, attrs) {
    private var mTitleView: TextView? = null
    private var mBackButton: View? = null
    private var mProgressBar: ProgressBar? = null
    private var mPopMenu: PopMenu? = null

    val menu: Menu
        get() = mPopMenu!!.menu

    var title: String
        get() = mTitleView!!.text.toString()
        set(s) {
            mTitleView!!.text = s
        }

    init {
        init()
    }

    private fun init() {
        View.inflate(mContext, R.layout.mini_toolbar_layout, this)
        mTitleView = findViewById(R.id.titleText)
        mBackButton = findViewById(R.id.back)
        mProgressBar = findViewById(R.id.progressBar)
        mProgressBar!!.max = MAX
    }

    fun populate(activity: AppCompatActivity, id: Int, s: String) {
        mBackButton!!.setOnClickListener { activity.onBackPressed() }
        mPopMenu = object :
            PopMenu(id, findViewById(R.id.moreOption), LONG_PRESS_CLICK, R.id.anchor) {
            override fun onClick(v: View) {
                popupMenu.show()
            }

            override fun onMenuItemClick(item: MenuItem): Boolean {
                return activity.onOptionsItemSelected(item)
            }
        }
        mPopMenu!!.showIcon()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            mPopMenu!!.popupMenu.gravity = Gravity.END
        title = s
    }

    @JvmOverloads
    fun setProgress(
        @IntRange(
            from = 0,
            to = MAX.toLong()
        ) progress: Int, animate: Boolean = false
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            mProgressBar!!.setProgress(progress, animate)
        else
            mProgressBar!!.progress = progress
        if (progress == MAX) mProgressBar!!.animate().alpha(0f).duration = ANIMATION_DURATION
    }

    companion object {
        private const val MAX = 10000
    }
}
