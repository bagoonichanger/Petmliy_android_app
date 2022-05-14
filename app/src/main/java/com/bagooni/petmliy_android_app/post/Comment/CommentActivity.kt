package com.bagooni.petmliy_android_app.post.Comment

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import com.bagooni.petmliy_android_app.databinding.ActivityCommentBinding
import com.bagooni.petmliy_android_app.post.RetrofitService

class CommentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentBinding
    lateinit var retrofitService: RetrofitService

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCommentBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.closeButton.setOnClickListener { finish() }

    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        return true
    }
}