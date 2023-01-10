package com.android.application
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

import com.android.application.whatsappclone.placeholder.PlaceholderContent.PlaceholderItem
import com.android.application.databinding.FragmentChatBinding

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class ChatFragmentRecyclerViewAdapter(private val userList:ArrayList<Friend>): RecyclerView.Adapter<ChatFragmentRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentChatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.contentView.text= userList[position].name
        holder.bind(userList[position])
    }

    override fun getItemCount(): Int = userList.size

    class ViewHolder(binding: FragmentChatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(friend: Friend) {
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ConversationActivity::class.java)
                intent.putExtra("name", friend.name)
                intent.putExtra("number", friend.number)
                itemView.context.startActivity(intent)
            }
        }

        val contentView: TextView = binding.content
    }

}