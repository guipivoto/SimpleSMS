package com.pivoto.simplesms.conversation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pivoto.simplesms.message.Message

@Composable
fun ConversationScreen(viewModel: ConversationScreenViewModel) {
    val conversationState = viewModel.conversationState.observeAsState()
    ConversationContent(state = conversationState)
}

@Composable
fun ConversationContent(state: State<ConversationState?>) {
    when (state.value) {
        is ConversationState.Loading -> LoadingConversation()
        is ConversationState.Empty -> EmptyConversation()
        is ConversationState.Loaded -> ConversationLoaded((state.value as ConversationState.Loaded).messageList)
        else -> Unit
    }
}

@Composable
private fun LoadingConversation() {
    Text(text = stringResource(id = R.string.conversation_loading_list))
}

@Composable
private fun EmptyConversation() {
    Text(text = stringResource(id = R.string.conversation_empty_list))
}

@Composable
private fun ConversationLoaded(messageList: List<Message>) {
    LazyColumn {
        items(messageList) { message ->
            MessageCard(message)
        }
    }
}

@Composable
fun MessageCard(msg: Message) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium, elevation = 1.dp
    ) {
        Column {
            Text(
                text = msg.address,
                color = MaterialTheme.colors.secondaryVariant,
                style = MaterialTheme.typography.subtitle2
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = msg.body ?: "null",
                modifier = Modifier.padding(all = 4.dp),
                style = MaterialTheme.typography.body2
            )
        }
    }
}