package com.android.application

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.widget.ViewPager2
import com.android.application.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewPager=findViewById<ViewPager2>(R.id.viewpager)
        val tabLayout=findViewById<TabLayout>(R.id.tablayout)
        val adapter= ViewPagerAdapter(supportFragmentManager,lifecycle)
        viewPager.adapter=adapter
        TabLayoutMediator(tabLayout,viewPager){
            tab,position->
            when(position){

                0->{tab.text="Chats"}
                1->{tab.text="Calls"}
                2->{tab.text="Camera"}
            }
        }.attach()

    }
}