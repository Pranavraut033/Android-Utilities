package pranav.views.photoPicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.preons.pranav.utilities.R
import kotlinx.android.synthetic.main.photopicker_option_dialog.view.*

class OptionDialog : BottomSheetDialogFragment() {

    private lateinit var navigationMenu: BottomNavigationView
    var itemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.photopicker_option_dialog, container, false).also {
            navigationMenu = it.option_menu
            navigationMenu.setOnNavigationItemSelectedListener(itemSelectedListener)

        }
    }
}
