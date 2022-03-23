package com.pivoto.simplesms.conversation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pivoto.simplesms.message.Message
import com.pivoto.simplesms.theme.MessageItemBodyPadding
import com.pivoto.simplesms.theme.MessageItemTitlePadding
import com.pivoto.simplesms.theme.TopBarTittlePadding

@Composable
fun ConversationScreen(viewModel: ConversationScreenViewModel) {
    val conversationState = viewModel.conversationState.observeAsState()
    ConversationContent(viewModel.conversationAddress, state = conversationState)
}

@Composable
fun ConversationContent(title : String, state: State<ConversationState?>) {
    Scaffold(
        topBar = {
            TopAppBar {
                Text(
                    modifier = Modifier.padding(TopBarTittlePadding),
                    text = title
                )
            }
        }
    ) {
        when (state.value) {
            is ConversationState.Loading -> LoadingConversation()
            is ConversationState.Empty -> EmptyConversation()
            is ConversationState.Loaded -> ConversationLoaded((state.value as ConversationState.Loaded).messageList)
            else -> Unit
        }
    }
}

@Composable
private fun LoadingConversation() {
    CircularProgressIndicator()
}

@Composable
private fun EmptyConversation() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = stringResource(id = R.string.conversation_empty_list))
    }
}

@Composable
private fun ConversationLoaded(messageList: List<Message>) {
    LazyColumn {
        items(
            messageList,
            key = { it.id }
        ) { message ->
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
                modifier = Modifier.padding(MessageItemTitlePadding),
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.subtitle1
            )

            Text(
                text = msg.body ?: "null",
                modifier = Modifier.padding(MessageItemBodyPadding),
                style = MaterialTheme.typography.body2
            )
        }
    }
}