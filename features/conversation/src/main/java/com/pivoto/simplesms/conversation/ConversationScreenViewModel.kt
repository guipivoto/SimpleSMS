package com.pivoto.simplesms.conversation

import androidx.lifecycle.*
import com.pivoto.simplesms.message.Message
import com.pivoto.simplesms.message.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    messageRepository: MessageRepository
) :
    ViewModel() {

    private val conversationAddress: String = savedStateHandle.get<String>("address") ?: ""
    private var _conversationState = MutableLiveData<ConversationState>(ConversationState.Idle)
    val conversationState: LiveData<ConversationState> = _conversationState

    init {
        viewModelScope.launch {
            _conversationState.value = ConversationState.Loading
            messageRepository.getConversation(conversationAddress).also {
                if(it.isNotEmpty()) {
                    _conversationState.value = ConversationState.Loaded(it)
                } else {
                    _conversationState.value = ConversationState.Empty
                }
            }
        }
    }
}

sealed class ConversationState {
    object Idle: ConversationState()
    object Loading: ConversationState()
    object Empty: ConversationState()
    data class Loaded(val messageList: List<Message>): ConversationState()
}