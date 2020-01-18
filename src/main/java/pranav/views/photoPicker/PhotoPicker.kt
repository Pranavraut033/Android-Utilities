package pranav.views.photoPicker

import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.preons.pranav.utilities.R
import java.io.File
import java.io.IOException
import kotlin.random.Random

class PhotoPicker(private val activity: AppCompatActivity, private val requestCode: Int) {

    private val optionDialog: OptionDialog = OptionDialog()
    var resultUri: Uri? = null

    fun picker() {
//		showOptions()
//		Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val intent = getPickImageChooserIntent()
        activity.startActivityForResult(intent, requestCode)
    }

    private fun showOptions() {
        optionDialog.show(activity.supportFragmentManager, "Menu options")
        optionDialog.itemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.option_camera -> {

                        true
                    }
                    R.id.option_camera -> {
                        true
                    }
                    else ->
                        false
                }
            }
    }

    private fun getPickImageChooserIntent(): Intent {

        // Determine Uri of camera image to save.
        val outputFileUri = getCaptureImageOutputUri()

        val allIntents = ArrayList<Intent>()
        val packageManager = activity.packageManager

        // collect all camera intents
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val listCam = packageManager.queryIntentActivities(captureIntent, 0)
        for (res in listCam) {
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
            }
            allIntents.add(intent)
        }

        // collect all gallery intents
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        val listGallery = packageManager.queryIntentActivities(galleryIntent, 0)
        for (res in listGallery) {
            val intent = Intent(galleryIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            allIntents.add(intent)
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        var mainIntent: Intent = allIntents[allIntents.size - 1]
        for (intent in allIntents) {
            if (intent.component!!.className == "com.android.documentsui.DocumentsActivity") {
                mainIntent = intent
                break
            }
        }
        allIntents.remove(mainIntent)

        // Create a chooser from the main intent
        val chooserIntent = Intent.createChooser(mainIntent, "Select source")

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toTypedArray())

        return chooserIntent
    }

    fun processResult(data: Intent?): Bitmap? {
        resultUri = getPickImageResultUri(data)
        return if (resultUri != null) {
            try {
                var bitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver, resultUri)
                bitmap = getResizedBitmap(bitmap, 1024)
                bitmap
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        } else {
            data!!.extras!!.get("data") as Bitmap
        }
    }

    private fun rotateImage(img: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        img.recycle()
        return rotatedImg
    }

    private fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 0) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    private fun getPickImageResultUri(data: Intent?): Uri? {
        var isCamera = true
        if (data != null) {
            val action = data.action
            isCamera = action != null && action == MediaStore.ACTION_IMAGE_CAPTURE
        }

        return if (isCamera) getCaptureImageOutputUri() else data!!.data
    }

    private fun getCaptureImageOutputUri(): Uri? {
        var outputFileUri: Uri? = null
        val getImage = activity.externalCacheDir
        if (getImage != null) {
            val temp = Math.abs(Random(12).nextInt()).toString() + "_" + System.currentTimeMillis()
            outputFileUri = Uri.fromFile(File(getImage.path, "$temp.jpg"))
        }
        return outputFileUri
    }
}
