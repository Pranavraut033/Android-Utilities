package pranav.utilities;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;

/**
 * Created on 03-02-2018 at 15:26 by Pranav Raut.
 * For Notes
 */

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class PopMenu implements View.OnTouchListener, View.OnLongClickListener,
        View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private final View anchor;
    protected final PopupMenu mPopupMenu;
    private float y, x, iy, ix;
    public static final int CLICK = 1;
    public static final int LONG_PRESS = 2;
    public static final int TOUCH = 4;
    public static final int TOUCH_CLICK = CLICK | TOUCH;
    public static final int LONG_PRESS_TOUCH = LONG_PRESS | TOUCH;
    public static final int LONG_PRESS_CLICK = CLICK | LONG_PRESS;
    public static final int ALL = CLICK | LONG_PRESS | TOUCH;
    protected Menu mMenu;

    public PopMenu(@MenuRes int menuId, @NonNull View view, int mode, int anchorID) {
        for (int i = 0, j; i < 3; i++) {
            j = (int) Math.pow(2, i);
            if ((mode ^ j) < mode) {
                mode ^= j;
                switch (j) {
                    case TOUCH:
                        view.setOnTouchListener(this);
                        break;
                    case CLICK:
                        view.setOnClickListener(this);
                        break;
                    case LONG_PRESS:
                        view.setOnLongClickListener(this);
                        break;
                }
            }
        }
        anchor = view.findViewById(anchorID);
        mPopupMenu = new PopupMenu(view.getContext(), anchor == null ? view : anchor);
        mPopupMenu.inflate(menuId);
        mMenu = mPopupMenu.getMenu();
        mPopupMenu.setOnMenuItemClickListener(this);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        x = event.getX();
        y = event.getY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                ix = x;
                iy = y;
                break;
            case MotionEvent.ACTION_UP:
                Utilities.ResourceManager res = new Utilities.ResourceManager(v.getContext());
                if (Math.abs(Math.sqrt(ix * ix + iy * iy) - Math.sqrt(x * x + y * y)) > res.getPx(48))
                    return v.performClick();
                break;
        }
        return false;
    }

    @Override
    public boolean onLongClick(View v) {
        Utilities.ResourceManager res = new Utilities.ResourceManager(v.getContext());
        if (anchor != null) {
            anchor.setX(x + res.getPx(20));
            anchor.setY(y);
        }
        mPopupMenu.show();
        return false;
    }

    public Menu getMenu() {
        return mMenu;
    }

    public void showIcon() {
        Utilities.INSTANCE.setForceShowIcon(mPopupMenu);
    }

    @Override
    public void onClick(View v) {
        // intentional left Blank can be override in object or child
    }

    @Override
    public abstract boolean onMenuItemClick(MenuItem item);

    public PopupMenu getPopupMenu() {
        return mPopupMenu;
    }
}
