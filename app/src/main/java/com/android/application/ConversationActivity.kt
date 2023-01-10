package com.android.application

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.R.*
import androidx.appcompat.R.id.home
import androidx.appcompat.R.id.icon
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class ConversationActivity : AppCompatActivity() {
    private val fcmApi = "https://fcm.googleapis.com/fcm/send"
    private val serverKey =
        "key=" + "AAAA0lo9bUg:APA91bEoRPSDmSwa235jFnfUUjursrF3NEx4_rchMyJU7nGDw4B_hn5zc-A3lBgpDdEZPiW9MxxtA9N8hZv66h0KWB_7_5qIQkTyGtPAllv8VNStZ3fL4erY5wjtOPInDRnB_-aPXV6n"
    private val contentType = "application/json"
    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(this)
    }
    private var token: String=""
    @SuppressLint("ResourceType")
    private lateinit var phoneNumber: String
    private lateinit var name: String
    private var receiverRoom: String? = null
    private var senderRoom: String? = null
    private lateinit var mDbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/Enter_your_topic_name")
        phoneNumber = intent.getStringExtra("number").toString()
        name = intent.getStringExtra("name").toString()
        Log.d("ConversationActivity", "onCreate: $phoneNumber $name")

        val actionBar = supportActionBar
        actionBar?.title = name
        actionBar?.subtitle = phoneNumber
        actionBar?.setIcon(R.drawable.ic_baseline_add_ic_call_24)
        actionBar?.setDisplayUseLogoEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        //MessageAdapter
        val messageRecyclerView =
            findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvMessage)
        val messageBox = findViewById<EditText>(R.id.etWriteMessage)
        val sendButton = findViewById<Button>(R.id.btnSendMessage)
        val messageList = ArrayList<Message>()
        val messageAdapter = MessageAdapter(messageList)
        messageRecyclerView.adapter = messageAdapter
        messageRecyclerView.layoutManager = LinearLayoutManager(this)
        //RoomId
        senderRoom = FirebaseAuth.getInstance().currentUser!!.phoneNumber + phoneNumber
        receiverRoom = phoneNumber + FirebaseAuth.getInstance().currentUser!!.phoneNumber
        mDbRef = FirebaseDatabase.getInstance().reference
        //Add Message to Adapter
        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : com.google.firebase.database.ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (snapshot1 in snapshot.children) {
                        val message = snapshot1.getValue(Message::class.java)

                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ConversationActivity, error.message, Toast.LENGTH_SHORT)
                        .show()
                }
            })
        //Send Button
        sendButton.setOnClickListener {
            Toast.makeText(this, "Send Button Clicked", Toast.LENGTH_SHORT).show()
            val message = messageBox.text.toString()
            if (message.isNotEmpty()) {
                val currentTime = Calendar.getInstance().time
                //Trimming the time to get only the time
                val time = currentTime.toString().substring(11, 19)
                Log.d("ConversationActivity", "onCreate current time: $time")
                val messageObject = Message(message, phoneNumber, time)
                mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                    .setValue(messageObject)
                    .addOnSuccessListener {
                        Log.d("ConversationActivity", "onCreate: Message Sent")
                        mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                            .setValue(messageObject)
                            .addOnSuccessListener {
                                Log.d("ConversationActivity", "onCreate: Message Received")
                                messageBox.setText("")
                            }
                        messageBox.setText("")
                    }
                //get token of receiver
                FirebaseFirestore.getInstance().collection("users").document(phoneNumber).get()
                    .addOnSuccessListener {
                        token = it.get("fcmToken").toString()
                        Log.d("ConversationActivity", "onCreate: $token")
                        //Send notification to receiver
                        sendNotification(message, token)
                    }


            }
        }
    }

    private fun sendNotification(message: String, token: String?) {
        val notification = JSONObject()
        val notifcitionBody = JSONObject()
        //Name of sender
        val name = FirebaseAuth.getInstance().currentUser!!.displayName
        Log.d("TAG","Name: $name")
        try {
            notifcitionBody.put("title", FirebaseAuth.getInstance().currentUser!!.phoneNumber)
            notifcitionBody.put("message", message)   //Enter your notification message
            Log.d("TAG","Token: $token")
            notification.put("to",token)
            notification.put("data", notifcitionBody)
            Log.e("TAG", "try")
        } catch (e: JSONException) {
            Log.e("TAG", "onCreate: " + e.message)
        }
        createJsonObject(notification)
    }

    private fun createJsonObject(notification: JSONObject) {
        Log.e("TAG", "sendNotification")
        val jsonObjectRequest = object : JsonObjectRequest(fcmApi, notification,
            Response.Listener{ response ->
                Log.i("TAG", "onResponse: $response")
            },
            Response.ErrorListener {
                Toast.makeText(this, "Request error", Toast.LENGTH_LONG).show()
                Log.i("TAG", "onErrorResponse: Didn't work")
            }) {

            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Authorization"] = serverKey
                params["Content-Type"] = contentType
                return params
            }
        }
        requestQueue.add(jsonObjectRequest)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        Log.d("ConversationActivity", "onBackPressed")
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("number", phoneNumber)
        intent.putExtra("name", name)
        startActivity(intent)
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.call -> {
                Toast.makeText(this, "Calls", Toast.LENGTH_SHORT).show()
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