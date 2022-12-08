package com.android.application

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.widget.ViewPager2
import com.android.application.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

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
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
//            R.id.menu_settings -> {
//                val intent = Intent(this, PersonalInfo::class.java)
//                startActivity(intent)
//                true
//            }
            R.id.menu_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginPage::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}