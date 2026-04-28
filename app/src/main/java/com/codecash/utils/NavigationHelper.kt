package com.codecash.utils

import android.app.Activity
import android.content.Intent
import com.codecash.*
import com.google.android.material.bottomnavigation.BottomNavigationView

object NavigationHelper {

    fun setupBottomNavigation(activity: Activity, bottomNav: BottomNavigationView, currentItemId: Int) {
        bottomNav.selectedItemId = currentItemId
        
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    if (currentItemId != R.id.nav_home) {
                        activity.startActivity(Intent(activity, DashboardActivity::class.java))
                        activity.finish()
                    }
                    true
                }
                R.id.nav_stats -> {
                    if (currentItemId != R.id.nav_stats) {
                        activity.startActivity(Intent(activity, StatsActivity::class.java))
                        activity.finish()
                    }
                    true
                }
                R.id.nav_budget -> {
                    if (currentItemId != R.id.nav_budget) {
                        activity.startActivity(Intent(activity, BudgetGoalsActivity::class.java))
                        activity.finish()
                    }
                    true
                }
                R.id.nav_forecast -> {
                    if (currentItemId != R.id.nav_forecast) {
                        activity.startActivity(Intent(activity, StatsActivity::class.java).apply {
                            putExtra("show_forecast", true)
                        })
                        activity.finish()
                    }
                    true
                }
                R.id.nav_settings -> {
                    if (currentItemId != R.id.nav_settings) {
                        activity.startActivity(Intent(activity, SettingsActivity::class.java))
                        activity.finish()
                    }
                    true
                }
                else -> false
            }
        }
    }
}