package com.pivoto.simplesms.inbox

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.pivoto.simplesms.message.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InboxScreenViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    messageRepository: MessageRepository
) :
    ViewModel() {

    private var _messageList = MutableLiveData(messageRepository.getMessages())
    val messageList: LiveData<String> = _messageList
}