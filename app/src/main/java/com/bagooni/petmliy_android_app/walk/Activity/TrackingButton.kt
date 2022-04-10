package com.bagooni.petmliy_android_app.walk.Activity

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import com.bagooni.petmliy_android_app.R

class TrackingButton(context: Context, attrs: AttributeSet) : AppCompatImageButton(context, attrs) {
    init {
        setBackgroundResource(R.drawable.walk_ic_free_icon_font_play)
        background
    }
    fun updateIconWithState(state: State) {
        when (state) {
            State.BEFORE_TRACKING -> {
                setImageResource(R.drawable.walk_ic_free_icon_font_play)
            }
            State.ON_TRACKING -> {
                setImageResource(R.drawable.walk_ic_free_icon_font_pause)
            }
            State.AFTER_TRACKING -> {
                setImageResource(R.drawable.walk_ic_free_icon_font_play)
            }
        }
    }
}