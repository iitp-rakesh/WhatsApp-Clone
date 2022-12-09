package com.android.application

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.R.*
import androidx.appcompat.R.id.home
import androidx.appcompat.R.id.icon
import androidx.appcompat.app.AppCompatActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import java.net.URL

class ConversationActivity : AppCompatActivity() {
    @SuppressLint("ResourceType")
    private lateinit var phoneNumber: String
    private lateinit var name: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)
        phoneNumber = intent.getStringExtra("number").toString()
        name = intent.getStringExtra("name").toString()
        Log.d("ConversationActivity", "onCreate: $phoneNumber $name")

        val actionBar = supportActionBar
        actionBar?.title = name
        actionBar?.subtitle = phoneNumber
        actionBar?.setIcon(R.drawable.ic_baseline_add_ic_call_24)
        actionBar?.setDisplayUseLogoEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.call -> {
                Toast.makeText(this, "Calls", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, CallActivity::class.java)
                intent.putExtra("number", phoneNumber)
                intent.putExtra("name", name)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            home -> {
                finish()
                true
            }
            icon -> {
                finish()
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }
}