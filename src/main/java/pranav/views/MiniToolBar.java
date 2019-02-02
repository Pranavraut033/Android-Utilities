package pranav.views;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.preons.pranav.utilities.R;

import pranav.utilities.PopMenu;

import static pranav.utilities.Animations.ANIMATION_DURATION;

/**
 * Created on 28-02-2018 at 00:47 by Pranav Raut.
 * For Notes
 */

public class MiniToolBar extends FrameLayout {
    private static final int MAX = 10_000;
    private final Context mContext;
    private TextView mTitleView;
    private View mBackButton;
    private ProgressBar mProgressBar;
    private PopMenu mPopMenu;

    public MiniToolBar(@NonNull Context context) {
        this(context, null);
    }

    public MiniToolBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        inflate(mContext, R.layout.mini_toolbar_layout, this);
        mTitleView = findViewById(R.id.titleText);
        mBackButton = findViewById(R.id.back);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setMax(MAX);
    }

    public void populate(@NonNull Activity activity, int id, String s) {
        mBackButton.setOnClickListener(v -> activity.onBackPressed());
        mPopMenu = new PopMenu(id, findViewById(R.id.moreOption), PopMenu.LONG_PRESS_CLICK, R.id.anchor) {
            @Override
            public void onClick(View v) {
                getPopupMenu().show();
            }

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return activity.onOptionsItemSelected(item);
            }
        };
        mPopMenu.showIcon();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            mPopMenu.getPopupMenu().setGravity(Gravity.END);
        setTitle(s);
    }

    public Menu getMenu() {
        return mPopMenu.getMenu();
    }

    public void setProgress(@IntRange(from = 0, to = MAX) int progress) {
        setProgress(progress, false);
    }

    public void setProgress(@IntRange(from = 0, to = MAX) int progress, boolean animate) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            mProgressBar.setProgress(progress, animate);
        else mProgressBar.setProgress(progress);
        if (progress == MAX) mProgressBar.animate().alpha(0).setDuration(ANIMATION_DURATION);
    }

    public void setTitle(String s) {
        mTitleView.setText(s);
    }

    public String getTitle() {
        return mTitleView.getText().toString();
    }
}
