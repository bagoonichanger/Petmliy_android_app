package com.bagooni.petmliy_android_app

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bagooni.petmliy_android_app.home.HomeFragment
import com.bagooni.petmliy_android_app.map.MapFragment
import com.bagooni.petmliy_android_app.post.PostFragment
import com.bagooni.petmliy_android_app.walk.WalkFragment

class viewPagerFragmentAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> PostFragment()
            2 -> WalkFragment()
            3 -> MapFragment()
            else -> error("No Fragment!")
        }
    }

}