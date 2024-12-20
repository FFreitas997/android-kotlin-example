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
import androidx.activity.viewModels
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
import com.ffreitas.flowify.utils.Constants
import com.ffreitas.flowify.utils.Constants.APPLICATION_PREFERENCE_NAME
import com.ffreitas.flowify.utils.Constants.SIGN_OUT_EXTRA
import com.ffreitas.flowify.utils.Constants.USER_PREFERENCE_NAME
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.serialization.json.Json

class HomeActivity : AppCompatActivity(), OnClickListener, OnMenuItemClickListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val viewModel: HomeActivityViewModel by viewModels { HomeActivityViewModel.Factory }
    private val json = Json { ignoreUnknownKeys = true }

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
        viewModel.user.observe(this) { user -> user?.let { updateUserInformation(it) } }
        sharedPreferences = getSharedPreferences(APPLICATION_PREFERENCE_NAME, MODE_PRIVATE)
    }

    private fun handleSignOut() {
        alertDialogSignOut {
            Log.d("HomeActivity", "Sign out")
            viewModel.signOut()
            val intent = Intent(this, AuthenticationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(SIGN_OUT_EXTRA, true)
            startActivity(intent)
            finish()
        }
    }

    private fun alertDialogSignOut(onPositive: () -> Unit) {
        AlertDialog
            .Builder(this)
            .setTitle(getString(R.string.sign_out))
            .setMessage(getString(R.string.sign_out_alert_dialog_message))
            .setIcon(ContextCompat.getDrawable(this, R.drawable.dangerous_24px))
            .setCancelable(true)
            .setPositiveButton("Yes") { log, _ -> log.dismiss(); onPositive() }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun updateUserInformation(user: User){
        Log.d("HomeActivity", "User found: ${user.email}")

        Glide
            .with(this)
            .load(user.picture)
            .centerCrop()
            .placeholder(R.drawable.person)
            .into(findViewById(R.id.account_image))

        findViewById<TextView>(R.id.account_name)
            .apply { text = user.name }

        val encoded = json.encodeToString(User.serializer(), user)
        sharedPreferences.edit()
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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .setAnchorView(R.id.fab).show()
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
}