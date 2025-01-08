package com.ffreitas.flowify.ui.home.components.board.create

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.ffreitas.flowify.R
import com.ffreitas.flowify.data.models.Board
import com.ffreitas.flowify.databinding.ActivityCreateBoardBinding
import com.ffreitas.flowify.utils.BackPressedCallback
import com.ffreitas.flowify.utils.ProgressDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import kotlinx.coroutines.launch

class CreateBoardActivity : AppCompatActivity() {

    private var _layout: ActivityCreateBoardBinding? = null
    private val layout get() = _layout!!

    private val model: CreateBoardViewModel by viewModels { CreateBoardViewModel.Factory }

    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val requestPicture = registerForActivityResult(PickVisualMedia()) { uri ->
        uri ?: return@registerForActivityResult
        handlePhotoSelection(uri)
    }

    private val requestForPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                requestPicture.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
            } else {
                handleErrorMessage(R.string.create_board_activity_permission_denied)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        _layout = ActivityCreateBoardBinding
            .inflate(layoutInflater)
            .also { setContentView(it.root) }

        ViewCompat.setOnApplyWindowInsetsListener(layout.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        progressDialog = ProgressDialog(this)
        firebaseAnalytics = Firebase.analytics

        setSupportActionBar(layout.toolbar)

        if (supportActionBar != null)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

        onBackPressedDispatcher
            .addCallback(this, BackPressedCallback(this))

        layout
            .toolbar
            .setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        layout
            .inputName
            .doAfterTextChanged {
                layout.inputNameLayout.error = null
                model.handleNameChanged(it)
            }

        layout
            .createBoardButton
            .setOnClickListener { onCreateBoard() }

        layout
            .boardImageContainer
            .setOnClickListener { onSelectPicture() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.state.collect { handleState(it) }
            }
        }
    }

    private fun onCreateBoard() {
        Log.d(TAG, "Creating board")
        if (!hasFormValid()) return
        model.createBoard()
    }

    private fun onSelectPicture() {
        Log.d(TAG, "Selecting picture")
        if (permissions.all { checkSelfPermission(it) == PERMISSION_GRANTED }) {
            requestPicture.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
            return
        }
        if (permissions.any { shouldShowRequestPermissionRationale(it) }) {
            handleErrorMessage(R.string.create_board_activity_permission_required)
            return
        }

        requestForPermissions.launch(permissions)
    }

    private fun handlePhotoSelection(uri: Uri) {
        Log.d(TAG, "Handling photo selection")

        model.handlePictureSelection(this, uri)

        Glide
            .with(this)
            .load(uri)
            .centerCrop()
            .placeholder(R.drawable.default_board)
            .into(layout.boardImage)
    }

    private fun hasFormValid(): Boolean {
        var isValid = true

        if (!model.isNameValid()) {
            layout.inputNameLayout.error = getString(R.string.create_board_activity_invalid_name)
            isValid = false
        }

        if (!model.isPictureValid()) {
            handleErrorMessage(R.string.create_board_activity_invalid_picture)
            isValid = false
        }

        return isValid
    }

    private fun handleState(state: CreateBoardUIState<Board>?) {
        when (state) {
            is CreateBoardUIState.Loading -> {
                progressDialog.show()
            }

            is CreateBoardUIState.Success -> {
                progressDialog.dismiss()
                handleSuccessState(state.result)
            }

            is CreateBoardUIState.Error -> {
                progressDialog.dismiss()
                handleErrorState(state.message)
            }

            else -> Unit
        }
    }

    private fun handleErrorState(message: String) {
        Log.d(TAG, "Error occurred: $message")
        firebaseAnalytics.logEvent("board_creation_error") {
            param("error_message", message)
        }
        handleErrorMessage(R.string.create_board_activity_error)
    }

    private fun handleSuccessState(result: Board) {
        Log.d(TAG, "Board created successfully")
        firebaseAnalytics.logEvent("board_created") {
            param("board_id", result.id)
            param("board_name", result.name)
        }
        finish()
    }

    private fun handleErrorMessage(@StringRes message: Int) {
        Snackbar
            .make(layout.root, getString(message), Snackbar.LENGTH_SHORT)
            .setBackgroundTint(resources.getColor(R.color.md_theme_error, null))
            .setTextColor(resources.getColor(R.color.md_theme_onError, null))
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _layout = null
    }

    companion object {
        private const val TAG = "CreateBoardActivity"

        private val permissions = when {
            VERSION.SDK_INT >= VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VISUAL_USER_SELECTED)
            }

            VERSION.SDK_INT >= VERSION_CODES.TIRAMISU -> {
                arrayOf(READ_MEDIA_IMAGES)
            }

            else -> {
                arrayOf(READ_EXTERNAL_STORAGE)
            }
        }
    }
}