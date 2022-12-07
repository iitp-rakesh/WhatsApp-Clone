package com.android.application
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.application.MainActivity
import com.android.application.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class LoginPage : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    private lateinit var auth: FirebaseAuth

    // we will use this to match the sent otp from firebase
    lateinit var storedVerificationId: String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)
        storedVerificationId=""
        val btnSendOTP = findViewById<Button>(R.id.btnSendOTP)
        val etPhoneNumber = findViewById<EditText>(R.id.etPhoneNumber)
        val etOTP = findViewById<EditText>(R.id.etOTP)
        val btnVerifyOTP = findViewById<Button>(R.id.btnVerifyOTP)
        btnVerifyOTP.visibility = Button.INVISIBLE
        etOTP.visibility = EditText.INVISIBLE
        auth = FirebaseAuth.getInstance()
        btnSendOTP.setOnClickListener {
            // below line is for checking whether the user
            // has entered his mobile number or not.
            btnVerifyOTP.visibility= Button.VISIBLE
            etOTP.visibility= EditText.VISIBLE
            btnSendOTP.text="Resend OTP"
            if (TextUtils.isEmpty(etPhoneNumber.text.toString())) {
                // when mobile number text field is empty
                // displaying a toast message.
                Toast.makeText(this, "Please enter a valid phone number.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // if the text field is not empty we are calling our
                // send OTP method for getting OTP from Firebase.
                val phone = "+91" + etPhoneNumber.text.toString()
                sendVerificationCode(phone)
            }
        }

        // Callback function for Phone Auth
        callbacks =
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                // This method is called when the verification is completed
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.d("GFG", "onVerificationCompleted Success")
                }

                // Called when verification is failed add log statement to see the exception
                override fun onVerificationFailed(e: FirebaseException) {
                    Log.d("GFG", "onVerificationFailed  $e")
                }

                // On code is sent by the firebase this method is called
                // in here we start a new activity where user can enter the OTP
                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    Toast.makeText(this@LoginPage, "OTP Sent", Toast.LENGTH_SHORT).show()
                    Log.d("GFG", "onCodeSent: $verificationId")
                    storedVerificationId = verificationId
                    resendToken = token
                }
            }
         // initializing on click listener
        // for verify otp button
        btnVerifyOTP.setOnClickListener{
                // validating if the OTP text field is empty or not.
                if (TextUtils.isEmpty(etOTP.text.toString())) {
                    // if the OTP text field is empty display
                    // a message to user to enter OTP
                    Toast.makeText(this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                } else {
                    // if OTP field is not empty calling
                    // method to verify the OTP.
                    verifyCode(etOTP.text.toString());
                }
            }
        }

    private fun verifyCode(OTP: String) {
        // creating the credential
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, OTP)
        // calling the method to verify the OTP.
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        // adding on complete listener for sign in process
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // if the sign in is successful we are
                    // starting the profile activity
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    // if the sign in is not successful then we are
                    // displaying an error message to the user.
                    Toast.makeText(this, "Wrong OTP", Toast.LENGTH_LONG).show()
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                }
            }
    }

    private fun sendVerificationCode(phone: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        Log.d("GFG", "Auth started")
    }
}