package com.bagooni.petmliy_android_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bagooni.petmliy_android_app.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.bagooni.petmliy_android_app.walk.Db.TrackingDAO
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() { // 수정 완료
    lateinit var trackingDAO: TrackingDAO

    private val bottomNavigationView: BottomNavigationView by lazy {
        findViewById<BottomNavigationView>(R.id.bottomNavigationView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigateToTrackingFragmentIfNeed(intent)


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.trackingFragment -> bottomNavigationView.visibility = View.GONE
                else -> bottomNavigationView.visibility = View.VISIBLE
            }
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> navController.navigate(R.id.homeFragment)
                R.id.story -> navController.navigate(R.id.postFragment)
                R.id.walk -> navController.navigate(R.id.walkFragment)
                R.id.map -> navController.navigate(R.id.mapFragment)
            }
            true
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeed(intent)
    }

    private fun navigateToTrackingFragmentIfNeed(intent: Intent?) {
        if(intent?.action == ACTION_SHOW_TRACKING_FRAGMENT) {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController
            navController.navigate(R.id.action_global_trackingFragment)
        }
    }
}