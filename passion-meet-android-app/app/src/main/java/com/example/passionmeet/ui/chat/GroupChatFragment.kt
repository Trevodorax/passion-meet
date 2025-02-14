package com.example.passionmeet.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passionmeet.R
import dagger.hilt.android.AndroidEntrypoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntrypoint
class GroupChatFragment : Fragment() {
    private val viewModel: GroupChatViewModel by viewModels()
    private lateinit var messageAdapter: ChatMessageAdapter
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_group_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get groupId from arguments
        arguments?.getLong("groupId")?.let { groupId ->
            viewModel.setGroupId(groupId)
        }

        setupViews(view)
        setupRecyclerView()
        observeMessages()
        observeErrors()
        setupMessageSending()
    }

    private fun setupViews(view: View) {
        messageInput = view.findViewById(R.id.messageInput)
        sendButton = view.findViewById(R.id.sendButton)
        recyclerView = view.findViewById(R.id.messagesRecyclerView)
    }

    private fun setupRecyclerView() {
        messageAdapter = ChatMessageAdapter(viewModel.getCurrentUserId())
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true
            }
            adapter = messageAdapter
        }
    }

    private fun observeMessages() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.messages.collectLatest { messages ->
                messageAdapter.submitList(messages) {
                    if (messages.isNotEmpty()) {
                        recyclerView.scrollToPosition(messages.size - 1)
                    }
                }
            }
        }
    }

    private fun observeErrors() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                error?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupMessageSending() {
        sendButton.setOnClickListener {
            val messageText = messageInput.text.toString().trim()
            if (messageText.isNotEmpty()) {
                viewModel.sendMessage(messageText)
                messageInput.text.clear()
            }
        }
    }

    companion object {
        fun newInstance(groupId: Long) = GroupChatFragment().apply {
            arguments = Bundle().apply {
                putLong("groupId", groupId)
            }
        }
    }
} 