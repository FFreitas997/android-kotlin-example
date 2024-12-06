package com.example.happyplaces.activities

import android.Manifest.permission.CAMERA
import android.app.Dialog
import android.content.ContentValues
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.happyplaces.HappyPlaceApplication
import com.example.happyplaces.R
import com.example.happyplaces.data.HappyPlaceModel
import com.example.happyplaces.databinding.ActivityCreateHappyPlaceBinding
import com.example.happyplaces.databinding.CameraDialogBinding
import com.example.happyplaces.repository.DefaultHappyPlaceRepository
import com.example.happyplaces.repository.HappyPlacesRepository
import com.example.happyplaces.utils.Constants
import com.example.happyplaces.utils.HappyPlaceMapper
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CreateHappyPlaceActivity : AppCompatActivity() {

    private val activityResultGallery =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri == null) {
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }
            layout?.ivPlaceImage?.setImageURI(uri)
            layout?.ivPlaceImage?.tag = uri
        }

    private val activityResultPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[CAMERA] == true)
                startCamera()
            else
                Toast
                    .makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }

    private val activityResultPlace =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(result.data!!)
                    layout?.etLocation?.setText(place.formattedAddress)
                    layout?.etLocation?.tag =
                        "${place.location?.latitude}€${place.location?.longitude}"
                }

                AutocompleteActivity.RESULT_ERROR -> {
                    val status = Autocomplete.getStatusFromIntent(result.data!!)
                    Log.e("CreateHappyPlaceActivity", "Error loading location: ${status.statusMessage}")
                    Toast.makeText(this, "Error loading location", Toast.LENGTH_SHORT).show()
                }

                RESULT_CANCELED -> {
                    Log.e("CreateHappyPlaceActivity", "Error loading location: User canceled")
                    Toast.makeText(this, "Error loading location: User canceled", Toast.LENGTH_SHORT).show()
                }
            }
        }

    private var layout: ActivityCreateHappyPlaceBinding? = null
    private var imageCapture: ImageCapture? = null
    private lateinit var datePicker: MaterialDatePicker<Long>
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var repository: HappyPlacesRepository
    private var placeID: Int? = null
    private var editModel: HappyPlaceModel? = null

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

        if (!Places.isInitialized())
            Places.initialize(applicationContext, getString(R.string.google_maps_api_key))

        val dao = (application as HappyPlaceApplication).db.happyPlaceDao()
        repository = DefaultHappyPlaceRepository(dao)
        cameraExecutor = Executors.newSingleThreadExecutor()

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
            val actions = arrayOf(
                getString(R.string.select_photo_form_gallery),
                getString(R.string.capture_photo_from_camera)
            )
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.select_action))
                .setItems(actions) { dialog, which ->
                    dialog.dismiss()
                    when (which) {
                        0 -> onSelectGallery()
                        1 -> onSelectCamera()
                        else -> return@setItems
                    }
                }
                .setIcon(android.R.drawable.ic_dialog_info)
                .setCancelable(false)
                .show()
        }

        layout?.btnSave?.setOnClickListener {

            when {
                layout?.etTitle?.text.isNullOrEmpty() -> {
                    Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                layout?.etDescription?.text.isNullOrEmpty() -> {
                    Toast.makeText(this, "Description is required", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                layout?.etLocation?.text.isNullOrEmpty() -> {
                    Toast.makeText(this, "Location is required", Toast.LENGTH_SHORT).show()
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

            val uri = layout?.ivPlaceImage?.tag.toString()

            val geoCodes = layout?.etLocation?.tag.toString().split("€")

            val image =
                if (uri.isNotEmpty())
                    convertURItoByteArray(uri)
                else
                    editModel?.image ?: byteArrayOf()

            val model = HappyPlaceModel(
                id = placeID,
                title = title,
                description = description,
                date = date,
                location = location,
                image = image ?: byteArrayOf(),
                latitude = geoCodes[0].toDouble(),
                longitude = geoCodes[1].toDouble()
            )

            if (placeID != null)
                onEditHappyPlace(model)
            else
                onCreateHappyPlace(model)
        }

        layout?.etLocation?.setOnClickListener {
            try {
                val fields = listOf(
                    Place.Field.ID, Place.Field.DISPLAY_NAME,
                    Place.Field.LOCATION, Place.Field.FORMATTED_ADDRESS
                )
                val intent = Autocomplete
                    .IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this)
                activityResultPlace.launch(intent)
            } catch (e: Exception) {
                Log.e("CreateHappyPlaceActivity", "Error loading location place", e)
                Toast.makeText(this, "Error loading location place", Toast.LENGTH_SHORT).show()
            }
        }

        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        layout?.etDate?.setText(sdf.format(cal.time))

        if (intent.hasExtra(Constants.EXTRA_PLACE_EDIT)) {
            placeID = intent
                .getIntExtra(Constants.EXTRA_PLACE_EDIT, 0)
            lifecycleScope.launch {
                repository
                    .readHappyPlace(placeID ?: 0)
                    .collect { handleUpdate(HappyPlaceMapper.mapEntityToModel(it)) }
            }
        }
    }

    private fun onCreateHappyPlace(model: HappyPlaceModel) {
        lifecycleScope.launch {
            repository.createHappyPlace(model)
            finish()
        }
    }

    private fun onEditHappyPlace(model: HappyPlaceModel) {
        lifecycleScope.launch {
            repository.updateHappyPlace(model)
            finish()
        }
    }

    private fun handleUpdate(model: HappyPlaceModel) {
        val bitmap = BitmapFactory
            .decodeByteArray(model.image, 0, model.image.size)
        supportActionBar?.title = getString(R.string.edit_happy_place)
        layout?.etTitle?.setText(model.title)
        layout?.etDescription?.setText(model.description)
        layout?.etDate?.setText(model.date)
        layout?.etLocation?.setText(model.location)
        layout?.etLocation?.tag = "${model.latitude}€${model.longitude}"
        layout?.ivPlaceImage?.setImageBitmap(bitmap)
        layout?.btnSave?.text = getString(R.string.update)
        this.editModel = model
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

    private fun startCamera() {
        val dialogCamera = Dialog(this)
        dialogCamera.setCancelable(false)
        dialogCamera.setCanceledOnTouchOutside(false)

        val bindingDialog = CameraDialogBinding
            .inflate(layoutInflater)
            .also { dialogCamera.setContentView(it.root) }

        bindingDialog.btnCancel
            .setOnClickListener { dialogCamera.dismiss() }

        bindingDialog.btnTakePicture
            .setOnClickListener { dialogCamera.dismiss(); captureCameraPhoto()}

        imageCapture = ImageCapture
            .Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
            .build()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview
                .Builder()
                .build()
                .also { it.surfaceProvider = bindingDialog.viewFinder.surfaceProvider }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture, preview)
                dialogCamera.show()
            } catch (e: Exception) {
                Log.e("CreateHappyPlaceActivity", "Error starting camera", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun captureCameraPhoto() {
        val imageCapture = imageCapture ?: return

        val filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS"

        val name = SimpleDateFormat(filenameFormat, Locale.getDefault())
            .format(System.currentTimeMillis())

        val contentValues = ContentValues()
            .apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P)
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }

        val outputOptions = ImageCapture
            .OutputFileOptions
            .Builder(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ).build()

        imageCapture
            .takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this),
                object : ImageCapture.OnImageSavedCallback {

                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        val uri = outputFileResults.savedUri ?: return
                        layout?.ivPlaceImage?.setImageURI(uri)
                        layout?.ivPlaceImage?.tag = uri
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e("CreateHappyPlaceActivity", "Error taking photo", exception)
                        Toast
                            .makeText(this@CreateHappyPlaceActivity, "Error taking photo", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            )
    }

    private fun onSelectGallery() {
        if (!ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(this)) {
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
        activityResultGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun onSelectCamera() {
        val cameraPermissionGranted = ContextCompat
            .checkSelfPermission(this, CAMERA) == PERMISSION_GRANTED

        if (cameraPermissionGranted) {
            startCamera()
            return
        }
        if (shouldShowRequestPermissionRationale(CAMERA)) {
            AlertDialog
                .Builder(this)
                .setTitle("Permission needed")
                .setMessage("Camera permission is needed to take a photo")
                .setCancelable(true)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
            return
        }
        activityResultPermissions.launch(arrayOf(CAMERA))
    }

    override fun onDestroy() {
        super.onDestroy()
        layout = null
        cameraExecutor.shutdown()
    }
}