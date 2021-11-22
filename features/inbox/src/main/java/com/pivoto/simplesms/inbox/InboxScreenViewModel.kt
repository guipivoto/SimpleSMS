package com.pivoto.simplesms.inbox

import androidx.lifecycle.*
import com.pivoto.simplesms.message.Message
import com.pivoto.simplesms.message.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InboxScreenViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    messageRepository: MessageRepository
) :
    ViewModel() {

    private var _inboxState = MutableLiveData<InboxState>(InboxState.Idle)
    val inboxState: LiveData<InboxState> = _inboxState

    init {
        viewModelScope.launch {
            _inboxState.value = InboxState.Loading
            val messageList = messageRepository.getConversations()
            _inboxState.value = if(messageList.isEmpty()) {
                InboxState.Empty
            } else {
                InboxState.Loaded(messageList)
            }
        }
    }
}

sealed class InboxState {
    object Idle: InboxState()
    object Loading: InboxState()
    object Empty: InboxState()
    data class Loaded(val messageList: List<Message>): InboxState()
}