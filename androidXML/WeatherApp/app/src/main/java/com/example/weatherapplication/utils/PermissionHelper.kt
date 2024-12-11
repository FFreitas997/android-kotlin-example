package com.example.weatherapplication.utils

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PermissionHelper(
    private val context: AppCompatActivity,
    private var mainDispatcher: MainCoroutineDispatcher = Dispatchers.Main
) {

    private var onSuccessListener: OnSuccessListener? = null

    private val activityForResultPermission = context
        .registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            var permissionsGranted = true
            it.entries.forEach { entry ->
                if (!entry.value) permissionsGranted = false
            }
            if (permissionsGranted)
                    (onSuccessListener ?: return@registerForActivityResult).onSuccess()
            else
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }

    fun requestPermission(permissions: Array<String>) {
        context.lifecycleScope.launch {
            val hasPermissionGranted = permissions
                .all { context.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }

            val shouldShowRequestPermissionRationale = permissions
                .any { context.shouldShowRequestPermissionRationale(it) }

            if (hasPermissionGranted) {
                (onSuccessListener ?: return@launch).onSuccess()
                return@launch
            }
            if (shouldShowRequestPermissionRationale) {
                AlertDialog
                    .Builder(context)
                    .setTitle("Permission required")
                    .setMessage("Please allow the permission to continue: ${permissions.joinToString(",")}")
                    .setCancelable(true)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Go to settings") { dialog, _ ->
                        dialog.dismiss()
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            .apply { data = Uri.fromParts("package", context.packageName, null) }
                            .let { context.startActivity(it) }
                    }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                    .show()
                return@launch
            }
            withContext(mainDispatcher) { activityForResultPermission.launch(permissions) }
        }
    }

    fun setOnSuccessListener(onSuccessListener: OnSuccessListener): PermissionHelper {
        this.onSuccessListener = onSuccessListener
        return this
    }
}

fun interface OnSuccessListener { fun onSuccess() }