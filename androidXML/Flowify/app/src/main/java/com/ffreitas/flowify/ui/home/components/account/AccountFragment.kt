package com.ffreitas.flowify.ui.home.components.account

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.ffreitas.flowify.R
import com.ffreitas.flowify.data.models.User
import com.ffreitas.flowify.databinding.FragmentAccountBinding
import com.ffreitas.flowify.utils.Constants
import com.ffreitas.flowify.utils.Constants.APPLICATION_PREFERENCE_NAME
import com.ffreitas.flowify.utils.ProgressDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

class AccountFragment : Fragment(), OnClickListener {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var _binding: FragmentAccountBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val model: AccountViewModel by viewModels() { AccountViewModel.Factory }
    private val json = Json { ignoreUnknownKeys = true }

    private val activityResultPermissions = requireActivity()
        .registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            handleRequestPermissionResult(it) {
                pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
            }
        }

    private val pickMedia = registerForActivityResult(PickVisualMedia()) {
        if (it == null) {
            Log.e(TAG, "Failed to get image from gallery.")
            return@registerForActivityResult
        }
        handlePhotoSelection(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val root: View = binding.root

        progressDialog = ProgressDialog(requireActivity())

        sharedPreferences =
            activity?.getSharedPreferences(APPLICATION_PREFERENCE_NAME, MODE_PRIVATE) ?: return root

        firebaseAnalytics = Firebase.analytics

        val userEncoded = sharedPreferences.getString(Constants.USER_PREFERENCE_NAME, null)

        binding.inputName.doOnTextChanged { text, _, _, _ -> model.handleChangeName(text) }
        binding.inputPhone.doOnTextChanged { text, _, _, _ -> model.handleChangePhone(text) }
        binding.accountButton.setOnClickListener(this)
        binding.accountImageContainer.setOnClickListener(this)

        if (!userEncoded.isNullOrEmpty())
            updateUI(userEncoded)

        model.accountUpdated.observe(viewLifecycleOwner) { hasSuccess ->
            progressDialog.dismiss()
            if (!hasSuccess) {
                handleErrorMessage()
                return@observe
            }
            Log.d(TAG, "User information updated successfully.")
            firebaseAnalytics.logEvent("account_information_updated") {
                param(FirebaseAnalytics.Param.LEVEL, "Account Information")
                param(FirebaseAnalytics.Param.SUCCESS, "true")
                param(FirebaseAnalytics.Param.CONTENT_TYPE, "User Information")
            }
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return root
    }

    private fun isFormValid(): Boolean {
        return when {
            !model.isNameValid() -> {
                binding.inputName.error = getString(R.string.account_fragment_name_error)
                false
            }

            !model.isPhoneValid() -> {
                binding.inputPhone.error = getString(R.string.account_fragment_phone_number_error)
                false
            }

            else -> {
                true
            }
        }
    }

    private fun updateUI(userEncoded: String) {
        val user = json.decodeFromString(User.serializer(), userEncoded)
        model.setCurrentUser(user)
        binding.inputName.setText(user.name)
        binding.inputPhone.setText(String.format(Locale.getDefault(), "%d", user.mobile))
        binding.inputEmail.setText(user.email)

        Glide
            .with(this)
            .load(user.picture)
            .centerCrop()
            .placeholder(R.drawable.person)
            .into(binding.accountImage)
    }

    private fun handleRequestPermissionResult(
        permissions: Map<String, Boolean>,
        permissionGrantedListener: () -> Unit
    ) {
        var permissionGranted = true
        permissions.entries.forEach {
            if (!it.value)
                permissionGranted = false
        }
        if (permissionGranted)
            permissionGrantedListener()
        else
            Toast.makeText(
                requireContext(),
                getString(R.string.account_fragment_permission_denied),
                Toast.LENGTH_SHORT
            ).show()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.account_button -> {
                onSubmit()
            }

            R.id.account_image_container -> {
                handleImageSelection()
            }
        }
    }


    @SuppressLint("InlinedApi")
    private fun handleImageSelection() {
        val permissionsUpsideDownCake = arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VISUAL_USER_SELECTED)
        val permissionsTiramisu = arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VISUAL_USER_SELECTED)
        val permissionsOlderVersions = arrayOf(READ_EXTERNAL_STORAGE)

        val hasPermissionGranted = when {
            VERSION.SDK_INT >= VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                permissionsUpsideDownCake.all { permission ->
                    requireContext().checkSelfPermission(permission) == PERMISSION_GRANTED
                }
            }

            VERSION.SDK_INT >= VERSION_CODES.TIRAMISU -> {
                permissionsTiramisu.all {
                    requireContext().checkSelfPermission(it) == PERMISSION_GRANTED
                }
            }

            else -> {
                permissionsOlderVersions.all {
                    requireContext().checkSelfPermission(it) == PERMISSION_GRANTED
                }
            }
        }

        val showRationale = when {
            VERSION.SDK_INT >= VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                permissionsUpsideDownCake.any { shouldShowRequestPermissionRationale(it) }
            }

            VERSION.SDK_INT >= VERSION_CODES.TIRAMISU -> {
                permissionsTiramisu.any { shouldShowRequestPermissionRationale(it) }
            }

            else -> {
                permissionsOlderVersions.any { shouldShowRequestPermissionRationale(it) }
            }
        }

        if (hasPermissionGranted) {
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
            return
        }
        if (showRationale) {
            AlertDialog
                .Builder(requireContext())
                .setTitle(getString(R.string.account_fragment_permission_title))
                .setMessage(getString(R.string.account_fragment_permission_text))
                .setCancelable(true)
                .setIcon(R.drawable.dangerous_24px)
                .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
            return
        }

        when {

            VERSION.SDK_INT >= VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                activityResultPermissions.launch(permissionsUpsideDownCake)
            }

            VERSION.SDK_INT >= VERSION_CODES.TIRAMISU -> {
                activityResultPermissions.launch(permissionsTiramisu)
            }

            else -> {
                activityResultPermissions.launch(permissionsOlderVersions)
            }
        }
    }

    private fun handlePhotoSelection(uri: Uri) {
        val filename = "${System.currentTimeMillis()}.jpg"
        val file = File(requireContext().filesDir, filename)

        try {

            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)

            inputStream?.close()
            outputStream.close()

            model.handlePictureSelection(file)

        } catch (e: Exception) {
            Log.e(TAG, "Error saving profile picture: ${e.message}")
        } finally {

            Glide
                .with(this)
                .load(uri)
                .centerCrop()
                .placeholder(R.drawable.person)
                .into(binding.accountImage)

        }
    }

    private fun onSubmit() {
        if (!isFormValid()) {
            handleErrorMessage()
            return
        }
        progressDialog.show()
        model.updateUserInformation()
    }

    private fun handleErrorMessage() {
        Snackbar
            .make(
                binding.root,
                getString(R.string.account_fragment_submit_error),
                Snackbar.LENGTH_SHORT
            )
            .setAction("Ok. I got it.") { }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "Account Fragment"
    }
}