package com.example.passionmeet.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.passionmeet.R
import com.example.passionmeet.data.local.entity.MessageEntity
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Locale

class ChatMessageAdapter(private val currentUserId: String) :
    ListAdapter<MessageEntity, ChatMessageAdapter.MessageViewHolder>(MessageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = getItem(position)
        holder.bind(message, message.senderId == currentUserId)
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val container: ConstraintLayout = itemView as ConstraintLayout
        private val senderNameText: TextView = itemView.findViewById(R.id.senderNameText)
        private val messageCard: MaterialCardView = itemView.findViewById(R.id.messageCard)
        private val messageText: TextView = itemView.findViewById(R.id.messageText)
        private val timeText: TextView = itemView.findViewById(R.id.timeText)
        private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        fun bind(message: MessageEntity, isCurrentUser: Boolean) {
            senderNameText.text = message.senderName
            messageText.text = message.content
            timeText.text = dateFormat.format(message.timestamp)

            // Apply different styles for sent vs received messages
            val constraintSet = ConstraintSet()
            constraintSet.clone(container)

            if (isCurrentUser) {
                messageCard.setCardBackgroundColor(itemView.context.getColor(R.color.teal_200))
                constraintSet.clear(R.id.messageCard, ConstraintSet.START)
                constraintSet.connect(R.id.messageCard, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
                constraintSet.connect(R.id.senderNameText, ConstraintSet.END, R.id.messageCard, ConstraintSet.END)
                constraintSet.clear(R.id.senderNameText, ConstraintSet.START)
                constraintSet.connect(R.id.timeText, ConstraintSet.END, R.id.messageCard, ConstraintSet.START)
                constraintSet.clear(R.id.timeText, ConstraintSet.START)
            } else {
                messageCard.setCardBackgroundColor(itemView.context.getColor(android.R.color.darker_gray))
                constraintSet.clear(R.id.messageCard, ConstraintSet.END)
                constraintSet.connect(R.id.messageCard, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                constraintSet.connect(R.id.senderNameText, ConstraintSet.START, R.id.messageCard, ConstraintSet.START)
                constraintSet.clear(R.id.senderNameText, ConstraintSet.END)
                constraintSet.connect(R.id.timeText, ConstraintSet.START, R.id.messageCard, ConstraintSet.END)
                constraintSet.clear(R.id.timeText, ConstraintSet.END)
            }

            constraintSet.applyTo(container)
        }
    }

    private class MessageDiffCallback : DiffUtil.ItemCallback<MessageEntity>() {
        override fun areItemsTheSame(oldItem: MessageEntity, newItem: MessageEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MessageEntity, newItem: MessageEntity): Boolean {
            return oldItem == newItem
        }
    }
} 