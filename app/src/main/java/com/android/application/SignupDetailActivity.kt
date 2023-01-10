package com.android.application

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth.*
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class SignupDetailActivity : AppCompatActivity() {
    private lateinit var imageUri: Uri
    private lateinit var image: ImageView
    private lateinit var phoneNumber: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_detail)
        val userName = findViewById<EditText>(R.id.etUserName)
        val btnNext = findViewById<Button>(R.id.btnSignupDetailNext)
        val chooseImage = findViewById<Button>(R.id.btnChooseImage)
        val db = FirebaseFirestore.getInstance()
        image = findViewById(R.id.ivChooseProfilePic)
        //set image
        phoneNumber = getInstance().currentUser?.phoneNumber.toString()
        // Taking the image from the user to upload
        chooseImage.setOnClickListener {
            pickImageFromGallery()
        }
        db.collection("users").document(phoneNumber).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject<UserDataClass>()
                    userName.setText(user!!.name)
                    Log.d("TAG", "onCreate User: $user")
                }
            }
            .addOnFailureListener {}
        btnNext.setOnClickListener {
            //Upload name and phone number to the database
            val user1 = UserDataClass(userName.text.toString(), phoneNumber)
            Log.d("TAG", "onCreate User: $user1")
            getInstance().currentUser!!.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(userName.text.toString())
                    .build()
            )
            db.collection("users").document(phoneNumber).set(user1, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("TAG", "DocumentSnapshot successfully written!")
                    Toast.makeText(this, "User Added", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.d("TAG", "Error writing document", e)
                    Toast.makeText(this, "User Added Failed", Toast.LENGTH_SHORT).show()
                }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        // Get the user image from the database
        val storageRef = FirebaseStorage.getInstance().reference.child("users/$phoneNumber")
        val localFile = File.createTempFile("images", "jpg")
        storageRef.getFile(localFile)
            .addOnSuccessListener {
                // Local temp file has been created
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                image.setImageBitmap(bitmap)
//                Toast.makeText(this, "Image Downloaded", Toast.LENGTH_SHORT).show()
                Log.d("TAG", "Image Downloaded")
            }.addOnFailureListener {
                // Handle any errors
//                Toast.makeText(this, "Image Downloading Failed", Toast.LENGTH_SHORT).show()
                Log.d("TAG", "Image Downloading Failed")
            }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"

        resultLauncher.launch(intent)

    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                imageUri = data?.data!!
                if (imageUri != null) {
                    image.setImageURI(imageUri)
                }
                val storageReference =
                    FirebaseStorage.getInstance().getReference("users/${phoneNumber}")
                storageReference.putFile(imageUri).addOnSuccessListener {
//                        Toast.makeText(this, "Image Uploaded", Toast.LENGTH_SHORT).show()
                    Log.d("TAG", "Image Uploaded")
                }
                    .addOnFailureListener {
//                        Toast.makeText(this, "Image Upload Failed", Toast.LENGTH_SHORT).show()
                        Log.d("TAG", "Image Upload Failed")
                    }
            }
        }
}
