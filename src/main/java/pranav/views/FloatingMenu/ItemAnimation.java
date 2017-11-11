package pranav.views.FloatingMenu;

import android.view.View;

public interface ItemAnimation {
    void openAnimation(View[] menuItem);

    void closeAnimation(View[] menuItem);

    void initialPosition(View[] menuItems);
}