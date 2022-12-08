package com.android.application

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.R.*
import androidx.appcompat.R.id.home
import androidx.appcompat.app.ActionBar
import com.android.application.R

class ConversationActivity : AppCompatActivity() {
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)
        val phoneNumber = intent.getStringExtra("number")
        val name = intent.getStringExtra("name")
        Log.d("ConversationActivity", "onCreate: $phoneNumber $name")
        val actionBar = supportActionBar
        actionBar?.title = name
        actionBar?.subtitle = phoneNumber
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            home -> {
                finish()
                true
            }
            else -> super.onContextItemSelected(item)
    }
    }
}