package com.android.application

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.messaging

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    lateinit var phoneNumber: String
    private val CHANNEL_ID = "84090"
    private val CHANNEL_NAME = "My Channel"
    private val CHANNEL_DESCRIPTION = "My Channel Description"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewPager = findViewById<ViewPager2>(R.id.viewpager)
        val tabLayout = findViewById<TabLayout>(R.id.tablayout)
        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = adapter
        phoneNumber = intent.getStringExtra("number").toString()
        Log.d("TAG", "Setting phone number: $phoneNumber")
        //Test
        val name = FirebaseAuth.getInstance().currentUser!!.displayName
        Log.d("TAG","Name: $name")
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            // Log and toast
            Log.d("TAG", token.toString())
            Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
            sendFcmRegistrationTokenToServer(token)
        })

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Chats"
                }
                1 -> {
                    tab.text = "Calls"
                }
                2 -> {
                    tab.text = "Camera"
                }
            }
        }.attach()
    }

    private fun sendFcmRegistrationTokenToServer(token: String?) {
        FirebaseFirestore.getInstance().collection("users").document(phoneNumber).update("fcmToken", token)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
                val intent = Intent(this, SignupDetailActivity::class.java)
                Log.d("TAG", "Setting phone number: $phoneNumber")
                intent.putExtra("number", phoneNumber)
                startActivity(intent)
                true
            }
            R.id.menu_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginPage::class.java)
                startActivity(intent)
                true
            }
            R.id.menu_search -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}