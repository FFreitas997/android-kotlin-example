package com.ffreitas.flowify.ui.home

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar.OnMenuItemClickListener
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.ffreitas.flowify.R
import com.ffreitas.flowify.data.models.User
import com.ffreitas.flowify.databinding.ActivityHomeBinding
import com.ffreitas.flowify.ui.authentication.AuthenticationActivity
import com.ffreitas.flowify.utils.Constants.APPLICATION_PREFERENCE_NAME
import com.ffreitas.flowify.utils.Constants.SIGN_OUT_EXTRA
import com.ffreitas.flowify.utils.Constants.USER_PREFERENCE_NAME
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class HomeActivity : AppCompatActivity(), OnClickListener, OnMenuItemClickListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    private val model: SharedViewModel by viewModels { SharedViewModel.Factory }
    private val json = Json { ignoreUnknownKeys = true }

    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences(APPLICATION_PREFERENCE_NAME, MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarHome.toolbar)
        binding.appBarHome.toolbar.setOnMenuItemClickListener(this)

        binding.appBarHome.fab.setOnClickListener(this)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration =
            AppBarConfiguration(
                setOf(R.id.nav_home, R.id.nav_account),
                drawerLayout
            )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        handleUIState()
    }

    private fun handleUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.state.collect { state ->
                    when (state) {
                        is UIState.Success -> {
                            Log.d(
                                TAG,
                                "The user information was found with email: ${state.user.email}"
                            )
                            updateUserInformation(state.user)
                        }

                        is UIState.Error -> {
                            Log.d(TAG, "Error: ${state.message}")
                            handleErrorMessage(R.string.home_activity_user_error)
                        }

                        else -> {
                            Log.d(TAG, "State not handled")
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        model.getCurrentUser()
        Log.d(TAG, "User information requested")
    }

    private fun handleErrorMessage(@StringRes message: Int) {
        Snackbar
            .make(binding.root, getString(message), Snackbar.LENGTH_SHORT)
            .setBackgroundTint(resources.getColor(R.color.md_theme_error, null))
            .setTextColor(resources.getColor(R.color.md_theme_onError, null))
            .setAnchorView(R.id.fab)
            .show()
    }

    private fun handleSignOut() {
        alertDialogSignOut {
            Log.d(TAG, "User signed out")
            model.signOut()
            Intent(this, AuthenticationActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra(SIGN_OUT_EXTRA, true)
                }
                .also {
                    startActivity(it)
                    finish()
                }
        }
    }

    private fun alertDialogSignOut(handler: () -> Unit) {
        AlertDialog
            .Builder(this)
            .setTitle(getString(R.string.sign_out))
            .setMessage(getString(R.string.sign_out_alert_dialog_message))
            .setIcon(ContextCompat.getDrawable(this, R.drawable.dangerous_24px))
            .setCancelable(true)
            .setPositiveButton("Yes") { log, _ -> log.dismiss(); handler() }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun updateUserInformation(user: User) {
        Log.d(TAG, "User found: ${user.email}")

        Glide
            .with(this)
            .load(user.picture)
            .centerCrop()
            .placeholder(R.drawable.person)
            .into(findViewById(R.id.account_image))

        findViewById<TextView>(R.id.account_name)
            .apply { text = user.name }

        val encoded = json.encodeToString(User.serializer(), user)
        sharedPreferences
            .edit()
            .putString(USER_PREFERENCE_NAME, encoded).apply()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.fab ->
                Toast.makeText(this, "Fab clicked", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out -> {
                handleSignOut(); true
            }

            else -> false
        }
    }

    companion object {
        private const val TAG = "HomeActivity"
    }
}