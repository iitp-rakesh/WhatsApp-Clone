package com.example.whatsappclone

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentActivity, lifecycle) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ChatFragment()
            1 -> CallFragment()
            2-> CameraFragment()
            else -> ChatFragment()
        }
    }
}