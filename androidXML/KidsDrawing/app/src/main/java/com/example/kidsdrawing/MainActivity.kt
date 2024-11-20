package com.example.kidsdrawing

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.forEach
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var drawingView: DrawingView
    private lateinit var currentPaint: ImageButton
    private lateinit var loadingDialog: Dialog

    private val openGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK && it.data != null) {
                val data = it.data
                val imageUri = data?.data
                if (imageUri != null) {
                    lifecycleScope.launch {
                        loadingDialog.show()
                        loadImageInBackground(imageUri)
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        drawingView = findViewById(R.id.drawing_view)
        drawingView.setSizeForBrush(20.toFloat())

        val linerLayout = findViewById<LinearLayout>(R.id.ll_color_palette)
        currentPaint = linerLayout.getChildAt(1) as ImageButton
        currentPaint.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pallet_pressed))
        linerLayout.forEach { (it as ImageButton).setOnClickListener { onClickPallet(it) } }

        val ibBackgroundSelector = findViewById<ImageButton>(R.id.ib_background_selector)
        ibBackgroundSelector.setOnClickListener { requestReadImagesPermission() }

        val saveBtn = findViewById<ImageButton>(R.id.ib_save)
        saveBtn.setOnClickListener { requestSaveImagePermission() }

        val ibBrush = findViewById<ImageButton>(R.id.ib_brush)
        ibBrush.setOnClickListener { showBrushSizeChooserDialog() }

        val undoBtn = findViewById<ImageButton>(R.id.ib_undo)
        undoBtn.setOnClickListener { onClickUndo() }

        Snackbar
            .make(findViewById(R.id.main), "Welcome to Kids Drawing App!", Snackbar.LENGTH_SHORT)
            .show()

        createLoadingDialog()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            Constants.REQUEST_CODE_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED)
                    saveCurrentDrawing()
                else
                    showAlertDialog("Permission denied", "You can't save the drawing without this permission.")
            }

            Constants.REQUEST_CODE_READ_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED)
                    loadBackgroundImage()
                else
                    showAlertDialog("Permission denied", "You can't set the background without this permission.")
            }
        }
    }

    private fun createLoadingDialog() {
        loadingDialog = Dialog(this)
        loadingDialog.setContentView(R.layout.dialog_loading)
        loadingDialog.setCancelable(false)
        loadingDialog.setCanceledOnTouchOutside(false)
    }

    private fun onClickUndo() { drawingView.undo() }

    private fun showBrushSizeChooserDialog() {
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush size:")
        brushDialog.setCancelable(true)

        val onSelect = { size: Float ->
            drawingView.setSizeForBrush(size)
            brushDialog.dismiss()
        }

        val smallBtn = brushDialog.findViewById<ImageButton>(R.id.ib_small_brush)
        smallBtn.setOnClickListener { onSelect(10.toFloat()) }

        val mediumBtn = brushDialog.findViewById<ImageButton>(R.id.ib_medium_brush)
        mediumBtn.setOnClickListener { onSelect(20.toFloat()) }

        val largeBtn = brushDialog.findViewById<ImageButton>(R.id.ib_large_brush)
        largeBtn.setOnClickListener { onSelect(30.toFloat()) }

        brushDialog.show()
    }

    private fun onClickPallet(view: View) {
        if (view === currentPaint)
            return
        val imageButton = view as ImageButton
        val colorTag = imageButton.tag.toString()
        drawingView.setColor(colorTag)
        currentPaint.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pallet_normal))
        imageButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pallet_pressed))
        currentPaint = imageButton
    }

    private fun showAlertDialog(title: String, message: String) {
        AlertDialog
            .Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun loadBackgroundImage() {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            .also { intent -> openGalleryLauncher.launch(intent) }
    }

    private suspend fun loadImageInBackground(uri: Uri) {
        withContext(Dispatchers.IO) {
            try {
                val bitmap = contentResolver
                    .openInputStream(uri)
                    ?.let { inputStream -> BitmapFactory.decodeStream(inputStream) }

                delay(1500)

                runOnUiThread {
                    loadingDialog.dismiss()
                    findViewById<ImageView>(R.id.iv_background)
                        .background = BitmapDrawable(resources, bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("MainActivity", "Error loading image: ${e.message}")
            }
        }
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null)
            bgDrawable.draw(canvas)
        else
            canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return returnedBitmap
    }

    private fun requestReadImagesPermission() {
        val listOfPermissions =
            if (VERSION.SDK_INT >= VERSION_CODES.UPSIDE_DOWN_CAKE)
                arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VISUAL_USER_SELECTED)
            else if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU)
                arrayOf(READ_MEDIA_IMAGES)
            else
                arrayOf(READ_EXTERNAL_STORAGE)

        val hasReadPermission = listOfPermissions
            .map { it -> ContextCompat.checkSelfPermission(this, it) == PERMISSION_GRANTED }
            .all { it }

        val shouldShowRequestPermissionRationale = listOfPermissions
            .map { it -> ActivityCompat.shouldShowRequestPermissionRationale(this, it) }
            .all { it }

        if (hasReadPermission) {
            loadBackgroundImage()
            return
        }

        if (shouldShowRequestPermissionRationale) {
            showAlertDialog("Permission needed", "We need permission to set the background.")
            return
        }

        ActivityCompat
            .requestPermissions(this, listOfPermissions, Constants.REQUEST_CODE_READ_EXTERNAL_STORAGE)
    }

    private fun requestSaveImagePermission() {
        if (VERSION.SDK_INT > VERSION_CODES.P) {
            saveCurrentDrawing()
            return
        }
        // For Android 10 and below versions of Android OS is needed to request permission to write external storage
        when {
            ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED -> {
                Log.d("MainActivity", "Permission granted")
            }
            ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE) -> {
                showAlertDialog("Permission needed", "We need permission to save the drawing.")
            }
            else -> {
                ActivityCompat
                    .requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), Constants.REQUEST_CODE_WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private fun saveCurrentDrawing() {
        loadingDialog.show()

        lifecycleScope
            .launch {
                val bitmap = getBitmapFromView(findViewById<FrameLayout>(R.id.fl_drawing_view))
                withContext(Dispatchers.IO) {
                    try {

                        val values = ContentValues()
                            .apply {
                                put(MediaStore.Images.Media.DISPLAY_NAME, "KidsDrawingApp_${(System.currentTimeMillis() / 1000)}.png")
                                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                            }

                        var imageURI = contentResolver
                            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

                        contentResolver
                            .openOutputStream(imageURI!!)
                            .use { out -> out?.let { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) } }

                        delay(1500)

                        withContext(Dispatchers.Main) {
                            loadingDialog.dismiss()
                            Toast
                                .makeText(this@MainActivity, "File saved successfully: $imageURI", Toast.LENGTH_LONG)
                                .show()
                            shareDrawing(imageURI)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("MainActivity", "Error saving image: ${e.message}")
                    }
                }
            }
    }

    private fun shareDrawing(result: Uri) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, result)
        intent.putExtra(Intent.EXTRA_TEXT, "This is the drawing I made with the kids drawing app. I would appreciate your feedback.")
        intent.setDataAndType(result, "image/png")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(Intent.createChooser(intent, "Share Drawing"))
    }
}