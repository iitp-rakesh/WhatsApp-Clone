package com.android.application

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * A fragment representing a list of Items.
 */
@Suppress("NAME_SHADOWING")
class ChatFragment : Fragment() {

    private lateinit var friend: ArrayList<Friend>


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_list, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        friend = ArrayList()
        val chatListAdapter = ChatFragmentRecyclerViewAdapter(friend)
        recyclerView.adapter = chatListAdapter
        refreshFragment(context!!)
        //Floating New Chat Button
        view.findViewById<FloatingActionButton>(R.id.btnNewChat)
            .setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
                startActivityForResult(intent, 1)
            }
        //Getting Friends list from Database
        FirebaseFirestore.getInstance().collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!).collection("Friends")
            .get().addOnSuccessListener {
            Log.d("Received", it.size().toString())
            for (document in it) {
                val name = document.get("name")
                val number = document.get("number")
                Log.d("Received", "$name $number")
                friend.add(Friend(number.toString(), name.toString()))
                chatListAdapter.notifyDataSetChanged()
            }
        }

        return view
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("Range", "Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val cursor1: Cursor
        //get data from intent
        val uri = data!!.data
        val detail: Array<String>?
        //handle intent results || calls when user from Intent (Contact Pick) picks or cancels pick contact
        //calls when user click a contact from contacts (intent) list
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
//                binding.contactTv.text = ""

            detail = arrayOf(
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            )
            cursor1 = requireActivity().contentResolver?.query(uri!!, detail, null, null, null)!!
//            cursor1 = requireActivity().contentResolver.query(uri!!, null, null, null, null)!!
            if (cursor1.moveToFirst()) {
                Log.d(
                    "TAG",
                    "onActivityResult: ${trimPhoneNumber(cursor1.getString(0))}${cursor1.getString(1)}"
                )
            }
            val intent = Intent(context, ConversationActivity::class.java)
            val name = cursor1.getString(1)
            val number = trimPhoneNumber(cursor1.getString(0))
            val friend = Friend(number, name)
            intent.putExtra("name", name)
            intent.putExtra("number", number)
            FirebaseFirestore.getInstance().collection("users")
                .document(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!)
                .collection("Friends").document(trimPhoneNumber(cursor1.getString(0))).set(friend)
            startActivity(intent)
            cursor1.close()
        }
    }

    //function to trim phone number
    private fun trimPhoneNumber(phoneNumber: String): String {
        var phoneNumber = phoneNumber.replace("[^0-9]".toRegex(), "")
        phoneNumber = if (phoneNumber.length == 10) {
            phoneNumber.replaceFirst("", "+91")
        } else {
            phoneNumber.replaceFirst("", "+")
        }
        return phoneNumber
    }

    private fun refreshFragment(context: Context) {
        context.let {
            val fragmentManager = (context as? AppCompatActivity)?.supportFragmentManager
            fragmentManager?.let {
                val currentFragment = fragmentManager.findFragmentById(R.id.viewpager)
                currentFragment?.let {
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.detach(it)

                    fragmentTransaction.attach(it)
                    fragmentTransaction.commit()

                }
            }
        }
    }
}
