package com.pivoto.simplesms.inbox

import androidx.compose.foundation.clickable
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
import com.pivoto.simplesms.inbox.nav.InboxScreenEvents
import com.pivoto.simplesms.message.Message
import com.pivoto.simplesms.theme.MessageItemBodyPadding
import com.pivoto.simplesms.theme.MessageItemTitlePadding
import com.pivoto.simplesms.theme.TopBarTittlePadding

@Composable
fun InboxScreen(viewModel: InboxScreenViewModel, actionHandler: InboxScreenEvents) {
    val inboxState = viewModel.inboxState.observeAsState()
    InboxContent(state = inboxState, actionHandler)
}

@Composable
fun InboxContent(state: State<InboxState?>, actionHandler: InboxScreenEvents) {
    Scaffold(
        topBar = {
            TopAppBar {
                Text(
                    modifier = Modifier.padding(TopBarTittlePadding),
                    text = stringResource(id = R.string.inbox_screen_tittle)
                )
            }
        }
    ) {
        when (state.value) {
            is InboxState.Loading -> LoadingInbox()
            is InboxState.Empty -> EmptyInbox()
            is InboxState.Loaded -> InboxLoaded(
                (state.value as InboxState.Loaded).messageList,
                actionHandler
            )
            else -> Unit
        }
    }
}

@Composable
private fun LoadingInbox() {
    CircularProgressIndicator()
}

@Composable
private fun EmptyInbox() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = stringResource(id = R.string.inbox_empty_list))
    }
}

@Composable
private fun InboxLoaded(messageList: List<Message>, actionHandler: InboxScreenEvents) {
    LazyColumn {
        items(
            messageList,
            key = { it.id }
        ) { message ->
            MessageCard(message, actionHandler)
        }
    }
}

@Composable
fun MessageCard(msg: Message, actionHandler: InboxScreenEvents) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium, elevation = 1.dp
    ) {
        Column(Modifier.clickable {
            actionHandler.onConversationOpened(msg.address)
        }) {
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

