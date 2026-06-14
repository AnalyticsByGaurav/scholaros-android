package com.scholaros.erp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.scholaros.erp.R
import com.scholaros.erp.data.local.SessionManager
import com.scholaros.erp.databinding.ActivityMainBinding
import com.scholaros.erp.databinding.NavHeaderBinding
import com.scholaros.erp.ui.auth.LoginActivity

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfig: AppBarConfiguration
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        session = SessionManager(this)

        setSupportActionBar(binding.toolbar)

        val navHost = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment?
            ?: throw IllegalStateException("NavHostFragment not found")
        navController = navHost.navController

        val topLevel = setOf(
            R.id.nav_dashboard, R.id.nav_notices, R.id.nav_attendance,
            R.id.nav_homework, R.id.nav_fees, R.id.nav_results,
            R.id.nav_timetable, R.id.nav_materials, R.id.nav_leave,
            R.id.nav_calendar, R.id.nav_complaints, R.id.nav_profile
        )
        appBarConfig = AppBarConfiguration(topLevel, binding.drawerLayout)

        setupActionBarWithNavController(navController, appBarConfig)
        binding.navView.setupWithNavController(navController)
        binding.bottomNav.setupWithNavController(navController)
        binding.navView.setNavigationItemSelectedListener(this)

        updateNavHeader()
    }

    private fun updateNavHeader() {
        val header = binding.navView.getHeaderView(0)
        val headerBinding = NavHeaderBinding.bind(header)
        headerBinding.tvNavName.text = session.userName.ifEmpty { "User" }
        headerBinding.tvNavEmail.text = session.userEmail
        headerBinding.tvNavRole.text = session.roleName.ifEmpty { session.userRole }
        val photo = session.photoUrl
        if (photo.isNotEmpty()) {
            Glide.with(this).load(photo).placeholder(R.mipmap.ic_launcher)
                .circleCrop().into(headerBinding.ivNavAvatar)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) {
            showLogoutDialog()
            return true
        }
        val handled = NavigationUI.onNavDestinationSelected(item, navController)
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return handled
    }

    private fun showLogoutDialog() {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                session.clearSession()
                startActivity(Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfig) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
