package com.bagooni.petmliy_android_app.walk.Fragment

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class CountUpView(context: Context, attrs: AttributeSet? = null) : AppCompatTextView(context, attrs) {
    private var timeValue = 0

    private val countUpAction: Runnable = object : Runnable {
        override fun run() {
            timeValue++
            updateCountTime(timeValue)
            handler?.postDelayed(this, 1000L)
        }
    }

    fun startCountUp() {
        handler?.post(countUpAction)
    }

    fun stopCountUp() {
        timeValue = 0
        handler?.removeCallbacks(countUpAction)
    }

    fun pauseCountUp() {
        handler?.removeCallbacks(countUpAction)
    }

    fun clearCountTime() {
        timeValue = 0
        updateCountTime(timeValue)
    }

    @SuppressLint("SetTextI18n")
    private fun updateCountTime(countTimeSeconds: Int) {
        val minutes = countTimeSeconds % 3600 / 60
        val seconds = countTimeSeconds % 60

        text = "%02d:%02d".format(minutes, seconds)
    }
}