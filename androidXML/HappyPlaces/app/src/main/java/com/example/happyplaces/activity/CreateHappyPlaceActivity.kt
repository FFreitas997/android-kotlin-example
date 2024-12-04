package com.example.happyplaces.activity

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.happyplaces.HappyPlaceApplication
import com.example.happyplaces.R
import com.example.happyplaces.data.HappyPlaceModel
import com.example.happyplaces.data.ImageType
import com.example.happyplaces.databinding.ActivityCreateHappyPlaceBinding
import com.example.happyplaces.repository.DefaultHappyPlaceRepository
import com.example.happyplaces.repository.HappyPlacesRepository
import com.example.happyplaces.utils.Constants.REQUEST_CODE_CAMERA
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CreateHappyPlaceActivity : AppCompatActivity() {

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri == null) {
                AlertDialog.Builder(this@CreateHappyPlaceActivity)
                    .setTitle("Error loading image")
                    .setMessage("An error occurred while loading the image. Please try again.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(true)
                    .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
                    .show()
                return@registerForActivityResult
            }
            layout?.ivPlaceImage?.setImageURI(uri)
            layout?.ivPlaceImage?.tag = "${ImageType.GALLERY}€$uri"
        }

    private var layout: ActivityCreateHappyPlaceBinding? = null
    private var imageCapture: ImageCapture? = null
    private lateinit var datePicker: MaterialDatePicker<Long>
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var repository: HappyPlacesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        layout = ActivityCreateHappyPlaceBinding.inflate(layoutInflater)
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

        val dao = (application as HappyPlaceApplication).db.happyPlaceDao()
        repository = DefaultHappyPlaceRepository(dao)

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
            AlertDialog.Builder(this)
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

            layout?.etTitle?.error = null
            layout?.etDescription?.error = null
            layout?.etLocation?.error = null
            layout?.etDate?.error = null

            when {
                layout?.etTitle?.text.isNullOrEmpty() -> {
                    layout?.etTitle?.error = "Title is required"
                    return@setOnClickListener
                }
                layout?.etDescription?.text.isNullOrEmpty() -> {
                    layout?.etDescription?.error = "Description is required"
                    return@setOnClickListener
                }
                layout?.etLocation?.text.isNullOrEmpty() -> {
                    layout?.etLocation?.error = "Location is required"
                    return@setOnClickListener
                }
                layout?.ivPlaceImage?.tag == null -> {
                    Toast.makeText(this, "Image is required", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            val title = layout?.etTitle?.text.toString().trim()
            val description = layout?.etDescription?.text.toString().trim()
            val date = layout?.etDate?.text.toString().trim()
            val location = layout?.etLocation?.text.toString().trim()

            val aux = layout?.ivPlaceImage?.tag.toString().split("€")

            val image = convertURItoByteArray(aux[1])

            val model = HappyPlaceModel(
                title = title,
                description = description,
                date = date,
                imageType = ImageType.valueOf(aux[0]),
                location = location,
                image = image ?: byteArrayOf(),
                latitude = 0.0,
                longitude = 0.0
            )

            lifecycleScope.launch(Dispatchers.Main) {
                repository.createHappyPlace(model)
                finish()
            }
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
        layout?.viewFinder?.setOnClickListener { captureCameraPhoto() }

        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        layout?.etDate?.setText(sdf.format(cal.time))
    }

    private fun convertURItoByteArray(uri: String): ByteArray? {
        try {
            val inputStream = contentResolver.openInputStream(Uri.parse(uri))
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap
                .compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            return byteArrayOutputStream.toByteArray()
        } catch (e: Exception) {
            Log.e("ImageLoading", "Error loading image data", e)
            return null
        }
    }

    private fun onSelectImageCamera() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
            return
        }
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            AlertDialog.Builder(this)
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
        val imageCapture = imageCapture ?: return

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

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
            .build()

        imageCapture
            .takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this),
                object : ImageCapture.OnImageSavedCallback {

                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        layout?.ivPlaceImage?.visibility = View.VISIBLE
                        layout?.viewFinder?.visibility = View.GONE
                        val uri = outputFileResults.savedUri ?: return
                        layout?.ivPlaceImage?.setImageURI(uri)
                        layout?.ivPlaceImage?.tag = "${ImageType.CAMERA}€$uri"
                    }

                    override fun onError(exception: ImageCaptureException) {
                        layout?.ivPlaceImage?.visibility = View.VISIBLE
                        layout?.viewFinder?.visibility = View.GONE
                        Log.e("CreateHappyPlaceActivity", "Error taking photo", exception)
                        AlertDialog.Builder(this@CreateHappyPlaceActivity)
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

    private fun startCamera() {
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
            .setTargetRotation(layout?.viewFinder?.display?.rotation ?: 0)
            .build()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also { it.surfaceProvider = layout?.viewFinder?.surfaceProvider }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture, preview)
                layout?.ivPlaceImage?.visibility = View.GONE
                layout?.viewFinder?.visibility = View.VISIBLE
            } catch (e: Exception) {
                Log.e("CreateHappyPlaceActivity", "Error starting camera", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun onSelectImageGallery() {
        if (!ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(this)) {
            AlertDialog.Builder(this)
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
                    startCamera()
                } else {
                    AlertDialog.Builder(this)
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
        cameraExecutor.shutdown()
    }
}