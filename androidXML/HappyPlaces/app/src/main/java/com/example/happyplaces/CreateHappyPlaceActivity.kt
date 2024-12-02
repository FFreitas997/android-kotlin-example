package com.example.happyplaces

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.Companion.isPhotoPickerAvailable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.happyplaces.data.HappyPlace
import com.example.happyplaces.databinding.ActivityCreateHappyPlaceBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Locale

private const val REQUEST_CODE_CAMERA = 1

class CreateHappyPlaceActivity : AppCompatActivity() {

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri == null) {
                AlertDialog
                    .Builder(this)
                    .setTitle("Error loading image")
                    .setMessage("An error occurred while loading the image. Please try again.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(true)
                    .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
                    .show()
                return@registerForActivityResult
            }
            layout?.ivPlaceImage?.setImageURI(uri)
            layout?.ivPlaceImage?.tag = uri.toString()
        }

    private var layout: ActivityCreateHappyPlaceBinding? = null
    private lateinit var datePicker: MaterialDatePicker<Long>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        layout = ActivityCreateHappyPlaceBinding
            .inflate(layoutInflater)
            .also { setContentView(it.root) }

        ViewCompat.setOnApplyWindowInsetsListener(layout?.root!!) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(layout?.toolbar)

        if (supportActionBar != null)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

        layout
            ?.toolbar
            ?.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val constraintsBuilder = CalendarConstraints
            .Builder()
            .setValidator(DateValidatorPointForward.now())
            .setStart(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker = MaterialDatePicker
            .Builder
            .datePicker()
            .setTitleText(getString(R.string.select_date))
            .setCalendarConstraints(constraintsBuilder)
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .build()

        layout?.etDate?.setOnClickListener {
            datePicker.show(supportFragmentManager, "DATE_PICKER")
        }

        datePicker.addOnPositiveButtonClickListener {
            layout?.etDate?.setText(datePicker.headerText)
        }

        layout?.ivPlaceImage?.setOnClickListener {
            val actions = arrayOf("Select photo form Gallery", "Capture photo from Camera")
            AlertDialog
                .Builder(this)
                .setTitle("Select Action")
                .setItems(actions) { dialog, which ->
                    dialog.dismiss()
                    when (which) {
                        0 -> onSelectImageGallery()
                        1 -> onSelectImageCamera()
                    }
                }
                .setIcon(android.R.drawable.ic_dialog_info)
                .setCancelable(false)
                .show()
        }

        layout?.btnSave?.setOnClickListener {
            val title = layout?.etTitle?.text.toString().trim()
            val description = layout?.etDescription?.text.toString().trim()
            val date = layout?.etDate?.text.toString().trim()
            val location = layout?.etLocation?.text.toString().trim()
            val image = layout?.ivPlaceImage?.tag.toString()

            val model = HappyPlace(
                id = -1,
                title = title,
                description = description,
                date = date,
                location = location,
                image = image
            )
        }
    }

    private fun onSelectImageCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            captureCameraPhoto()
            return
        }
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            AlertDialog
                .Builder(this)
                .setTitle("Permission needed")
                .setMessage("Camera permission is needed to take a photo")
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
            return
        }
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_CODE_CAMERA
        )
    }

    private fun captureCameraPhoto() {
        val imageCapture = ImageCapture
            .Builder()
            .build()

        val filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS"

        val name = SimpleDateFormat(filenameFormat, Locale.getDefault())
            .format(System.currentTimeMillis())

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        val outputOptions = ImageCapture
            .OutputFileOptions
            .Builder(contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            .build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val uri = outputFileResults.savedUri ?: return
                    layout?.ivPlaceImage?.setImageURI(uri)
                    layout?.ivPlaceImage?.tag = uri.toString()
                }

                override fun onError(exception: ImageCaptureException) {
                    AlertDialog
                        .Builder(this@CreateHappyPlaceActivity)
                        .setTitle("Error taking photo")
                        .setMessage("An error occurred while taking the photo. Please try again.")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(true)
                        .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
                        .show()
                }
            }
        )
    }

    private fun onSelectImageGallery() {
        if (!isPhotoPickerAvailable(this)) {
            AlertDialog
                .Builder(this)
                .setTitle("Error loading image")
                .setMessage("An error occurred while loading the image. Please try again.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(true)
                .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
                .show()
            return
        }
        imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    captureCameraPhoto()
                } else {
                    AlertDialog
                        .Builder(this)
                        .setTitle("Permission denied")
                        .setMessage("Camera permission is needed to take a photo")
                        .setCancelable(false)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        layout = null
    }
}