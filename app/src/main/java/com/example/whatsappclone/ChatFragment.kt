package com.android.application

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.contentValuesOf
import com.example.whatsappclone.placeholder.PlaceholderContent
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * A fragment representing a list of Items.
 */
class ChatFragment : Fragment() {

    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_list, container, false)
        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = ChatFragmentRecyckerViewAdapter(PlaceholderContent.ITEMS)
            }
        }
        //Floating New Chat Button
        view.findViewById<FloatingActionButton>(R.id.btnNewChat)
            .setOnClickListener(View.OnClickListener {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
                startActivityForResult(intent, 1)
            })
        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("Range", "Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val cursor1: Cursor
        //get data from intent
        val uri = data!!.data
        var detail: Array<String>? = null
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
            intent.putExtra("name", cursor1.getString(1))
            intent.putExtra("number", trimPhoneNumber(cursor1.getString(0)))
            startActivity(intent)
            cursor1.close()
        }
    }
//function to trim phone number
    private fun trimPhoneNumber(phoneNumber: String): String {
        var phoneNumber = phoneNumber.replace("[^0-9]".toRegex(), "")
        if (phoneNumber.length == 10) {
            phoneNumber = phoneNumber.replaceFirst("", "+91")
        } else {
            phoneNumber = phoneNumber.replaceFirst("", "+")
        }
        return phoneNumber
    }
}