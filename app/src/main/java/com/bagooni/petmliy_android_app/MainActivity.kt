package com.bagooni.petmliy_android_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.bagooni.petmliy_android_app.databinding.ActivityMainBinding
import com.bagooni.petmliy_android_app.home.HomeFragment
import com.bagooni.petmliy_android_app.map.MapFragment
import com.bagooni.petmliy_android_app.post.PostFragment
import com.bagooni.petmliy_android_app.walk.WalkFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val homeFragment: HomeFragment by lazy {
        HomeFragment()
    }

    private val postFragment: PostFragment by lazy {
        PostFragment()
    }

    private val walkFragment: WalkFragment by lazy {
        WalkFragment()
    }

    private val mapFragment: MapFragment by lazy {
        MapFragment()
    }

    private val bottomNavigationView: BottomNavigationView by lazy {
        binding.bottomNavigationView
    }

    override fun onCreate(savedInstanceState: Bundle?) { //checkout commit0303-2
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        replaceFragment(homeFragment) // 처음 페이지

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> replaceFragment(homeFragment)
                R.id.story -> replaceFragment(postFragment)
                R.id.walk -> replaceFragment(walkFragment)
                R.id.map -> replaceFragment(mapFragment)
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .apply {
                replace(R.id.fragmentContainer, fragment)
                commit()
            }
    }
}