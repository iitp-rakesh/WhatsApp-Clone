package com.android.application

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig

class CallActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
        val appId=826213993
        val appSign="2e0c41dd95e9c550eef0d1f50b0fbd91cc7fb4d52b14c1816514d872d7dc2f5e"
        val name=intent.getStringExtra("name")
        val number=intent.getStringExtra("number")
    }
}