package com.android.application

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.widget.ViewPager2
import com.android.application.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    lateinit var phoneNumber: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewPager=findViewById<ViewPager2>(R.id.viewpager)
        val tabLayout=findViewById<TabLayout>(R.id.tablayout)
        val adapter= ViewPagerAdapter(supportFragmentManager,lifecycle)
        viewPager.adapter=adapter
        phoneNumber= intent.getStringExtra("number").toString()
        Log.d("TAG", "Setting phone numberz: $phoneNumber")
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
            R.id.menu_settings -> {
                val intent = Intent(this, SignupDetailActivity::class.java)
                Log.d("TAG", "Setting phone number: $phoneNumber")
                intent.putExtra("number",phoneNumber)
                startActivity(intent)
                true
            }
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