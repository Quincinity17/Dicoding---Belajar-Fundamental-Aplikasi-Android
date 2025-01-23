package com.example.halfsubmission.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.halfsubmission.R
import com.example.halfsubmission.data.preferences.SettingPreferences
import com.example.halfsubmission.data.preferences.dataStore
import com.example.halfsubmission.databinding.ActivityMainBinding
import com.example.halfsubmission.ui.Setting.SettingViewModel
import com.example.halfsubmission.ui.Setting.ViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_Upcoming, R.id.navigation_Finished
            )
        )

        val pref = SettingPreferences.getInstance(applicationContext.dataStore)
        val settingViewModel = ViewModelProvider(this, ViewModelFactory(pref)).get(SettingViewModel::class.java)

        settingViewModel.getThemeSettings().observe(this) { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    navController.navigate(R.id.navigation_home)
                    true
                }
                R.id.navigation_Upcoming -> {
                    navController.navigate(R.id.navigation_Upcoming)
                    true
                }
                R.id.navigation_Finished -> {
                    navController.navigate(R.id.navigation_Finished)
                    true
                }
                R.id.navigation_Setting -> {
                    navController.navigate(R.id.navigation_Setting)
                    true
                }
                else -> false
            }
        }
    }
}