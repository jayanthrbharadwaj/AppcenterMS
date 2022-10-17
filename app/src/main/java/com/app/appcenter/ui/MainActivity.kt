package com.app.appcenter.ui

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.app.appcenter.R
import com.app.appcenter.databinding.ActivityMainBinding
import com.app.appcenter.ui.dashboard.DownloadsFragment
import com.app.appcenter.ui.home.HomeFragment
import com.app.appcenter.ui.notifications.NotificationsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks,
EasyPermissions.RationaleCallbacks{

    lateinit var binding: ActivityMainBinding
    private val RC_LOCATION_CONTACTS_PERM = 124
    lateinit var homeFragment: HomeFragment
    lateinit var downloadsFragment: DownloadsFragment
    lateinit var notificationsFragment: NotificationsFragment
    lateinit var fmManager: FragmentManager
    lateinit var activeFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeFragment = HomeFragment()
        downloadsFragment = DownloadsFragment()
        notificationsFragment = NotificationsFragment()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fmManager = supportFragmentManager
            val perms =
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            EasyPermissions.requestPermissions(
                this@MainActivity, getString(R.string.rationale_write),
                RC_LOCATION_CONTACTS_PERM, *perms
            )
        fmManager.beginTransaction().apply {
            add(R.id.nav_host_fragment_activity_main, notificationsFragment, "3")
            add(R.id.nav_host_fragment_activity_main, downloadsFragment, "2")
            add(R.id.nav_host_fragment_activity_main, homeFragment, "1").commit()
            activeFragment = homeFragment
        }
        setUpBottomNavigationView()
    }

    private fun setUpBottomNavigationView() {
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.apply {
            setupWithNavController(navController)
            setOnItemReselectedListener {
                Log.d("Reselected", "reselected")
            }
            setOnNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.navigation_home -> {
                        fmManager.beginTransaction().hide(activeFragment).show(homeFragment)
                            .commit()
                        activeFragment = homeFragment
                        true
                    }
                    R.id.navigation_dashboard -> {
                        fmManager.beginTransaction().hide(activeFragment)
                            .show(downloadsFragment).commit()
                        activeFragment = downloadsFragment
                        true
                    }
                    R.id.navigation_notifications -> {
                        fmManager.beginTransaction().hide(activeFragment)
                            .show(notificationsFragment).commit()
                        activeFragment = notificationsFragment
                        true
                    }
                    else -> false
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        finish()
    }

    @AfterPermissionGranted(124)
    private fun methodRequiresTwoPermission() {
        val perms =
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            // Already have permission, do the thing
            // ...
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                this@MainActivity, getString(R.string.rationale_write),
                RC_LOCATION_CONTACTS_PERM, *perms
            )
        }
    }

    override fun onRationaleAccepted(requestCode: Int) {
        // Continue
    }

    override fun onRationaleDenied(requestCode: Int) {
        finish()
    }

}