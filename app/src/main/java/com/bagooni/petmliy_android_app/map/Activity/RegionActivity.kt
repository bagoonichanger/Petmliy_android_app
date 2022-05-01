package com.bagooni.petmliy_android_app.map.Activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bagooni.petmliy_android_app.R
import com.bagooni.petmliy_android_app.databinding.ActivityRegionBinding

class RegionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRegionBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setToolbar()
        setSpinner()

        binding.toolbar.setNavigationOnClickListener { // 뒤로가기 버튼
            finish()
        }

    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setSpinner() {
        val spinner: Spinner = findViewById(R.id.regionSpinner)

        ArrayAdapter.createFromResource(this, R.array.region_array, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        binding.regionButton.setOnClickListener {
            if(spinner.selectedItem.toString() == "지역을 선택하세요"){
                Toast.makeText(this, spinner.selectedItem.toString(), Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, spinner.selectedItem.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.region_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.industry -> {
                startActivity(Intent(this, IndustryActivity::class.java))
                finish()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}