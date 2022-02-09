package com.bagooni.petmliy_android_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.bagooni.petmliy_android_app.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val bottomNavigationView: BottomNavigationView by lazy {
        binding.bottomNavigationView
    }

    private val viewpager: ViewPager2 by lazy {
        binding.viewPager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewpager.adapter = viewPagerFragmentAdapter(this)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> viewpager.currentItem = 0
                R.id.story -> viewpager.currentItem = 1
                R.id.walk -> viewpager.currentItem = 2
                R.id.map -> viewpager.currentItem = 3
            }
            false
        }

        viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bottomNavigationView.menu.getItem(position).isChecked = true
            }
        })

    }
}