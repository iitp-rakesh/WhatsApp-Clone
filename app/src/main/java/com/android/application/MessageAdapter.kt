package com.android.application


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(private val messageList: ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 2) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.sent_message, parent, false)
            SentMessageHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.received_message, parent, false)
            ReceivedMessageHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        if (holder.javaClass == SentMessageHolder::class.java) {
            (holder as SentMessageHolder)
            holder.sentMessage.text = currentMessage.message
            holder.sentTime.text = currentMessage.timestamp
        } else {
            (holder as ReceivedMessageHolder)
            holder.receivedMessage.text = currentMessage.message
            holder.receivedTime.text = currentMessage.timestamp
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        return if (currentMessage.senderId == FirebaseAuth.getInstance().currentUser!!.phoneNumber) {
            1
        } else {
            2
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }


    class SentMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentMessage = itemView.findViewById<TextView>(R.id.sent_message_text)!!
        val sentTime = itemView.findViewById<TextView>(R.id.sent_message_time)!!
    }

    class ReceivedMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receivedMessage = itemView.findViewById<TextView>(R.id.received_message_text)!!
        val receivedTime = itemView.findViewById<TextView>(R.id.received_message_time)!!
    }

}