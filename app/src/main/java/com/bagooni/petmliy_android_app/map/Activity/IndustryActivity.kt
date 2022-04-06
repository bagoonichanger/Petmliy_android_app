package com.bagooni.petmliy_android_app.map.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.bagooni.petmliy_android_app.R
import com.bagooni.petmliy_android_app.databinding.ActivityIndustryBinding

class IndustryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIndustryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityIndustryBinding.inflate(layoutInflater)
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
        val spinner: Spinner = findViewById(R.id.industrySpinner)

        ArrayAdapter.createFromResource(this, R.array.industry_array, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        binding.industryButton.setOnClickListener {
            if(spinner.selectedItem.toString() == "업종을 선택하세요"){
                Toast.makeText(this, spinner.selectedItem.toString(), Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, spinner.selectedItem.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.industry_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.region -> {
                startActivity(Intent(this, RegionActivity::class.java))
                finish()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}