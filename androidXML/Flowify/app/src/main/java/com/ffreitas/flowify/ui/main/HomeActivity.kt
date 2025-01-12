package com.ffreitas.flowify.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar.OnMenuItemClickListener
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
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
import com.ffreitas.flowify.ui.main.components.board.create.CreateBoardActivity
import com.ffreitas.flowify.utils.Constants.SIGN_OUT_EXTRA
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), OnMenuItemClickListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    private val model by viewModels<SharedViewModel>()

    private val createActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK)
                model.signalBoardsFetch()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarHome.toolbar)
        binding.appBarHome.toolbar.setOnMenuItemClickListener(this)

        binding.appBarHome.fab.setOnClickListener { handleCreateButton() }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration =
            AppBarConfiguration(
                setOf(R.id.nav_boards, R.id.nav_account),
                drawerLayout
            )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        model.state.observe(this) { handleState(it) }
    }

    private fun handleCreateButton() {
        val intent = Intent(this, CreateBoardActivity::class.java)
        createActivityLauncher.launch(intent)
    }

    private fun handleState(state: HomeUIState<User>) {
        when (state) {
            is HomeUIState.Success -> {
                Log.d(TAG, "User found with email: ${state.data.email}")
                updateUserInformation(state.data)
            }

            is HomeUIState.Error -> {
                Log.d(TAG, "Error occurred: ${state.message}")
                handleErrorMessage(R.string.home_activity_user_error)
                finish()
            }

            else -> Unit
        }
    }

    private fun handleErrorMessage(@StringRes message: Int) {
        Snackbar
            .make(binding.root, getString(message), Snackbar.LENGTH_SHORT)
            .setBackgroundTint(resources.getColor(R.color.md_theme_error, null))
            .setTextColor(resources.getColor(R.color.md_theme_onError, null))
            .setAnchorView(R.id.fab)
            .show()
    }

    private fun signOut() {
        Log.d(TAG, "User signed out")
        model.signOut()
        val intent = Intent(this, AuthenticationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(SIGN_OUT_EXTRA, true)
        startActivity(intent)
        finish()
    }

    private fun handleSignOut() {
        AlertDialog
            .Builder(this)
            .setTitle(getString(R.string.sign_out))
            .setMessage(getString(R.string.sign_out_alert_dialog_message))
            .setIcon(ContextCompat.getDrawable(this, R.drawable.dangerous_24px))
            .setCancelable(true)
            .setPositiveButton("Yes") { log, _ -> log.dismiss(); signOut() }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun updateUserInformation(user: User) {
        Log.d(TAG, "User found: ${user.email}")
        binding
            .navView
            .getHeaderView(0).apply {
                findViewById<ShapeableImageView>(R.id.account_image)
                    .let {
                        Glide
                            .with(this)
                            .load(user.picture)
                            .centerCrop()
                            .placeholder(R.drawable.person)
                            .into(it)
                    }
                findViewById<TextView>(R.id.account_name)
                    .apply { text = user.name }
            }
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