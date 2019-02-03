package pranav.views.TextField;

import androidx.annotation.IntDef;
import android.text.InputType;

/**
 * Created on 29-07-2017 at 00:08 by Pranav Raut.
 * For QRCodeProtection
 */

@IntDef(value = {InputType.TYPE_CLASS_TEXT,
        InputType.TYPE_CLASS_NUMBER,
        InputType.TYPE_CLASS_PHONE,
        InputType.TYPE_TEXT_FLAG_CAP_WORDS,
        InputType.TYPE_TEXT_VARIATION_PASSWORD,
        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD,
        InputType.TYPE_NUMBER_VARIATION_PASSWORD,
        InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE,
        InputType.TYPE_TEXT_FLAG_AUTO_CORRECT})
@interface a {
}

