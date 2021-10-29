package com.pivoto.simplesms.inbox

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
fun InboxScreen(viewModel: InboxScreenViewModel) {
    var inboxState = viewModel.inboxState.observeAsState()
    InboxContent(state = inboxState)
}

@Composable
fun InboxContent(state: State<InboxState?>) {
    when (state.value) {
        is InboxState.Loading -> LoadingInbox()
        is InboxState.Empty -> EmptyInbox()
        is InboxState.Loaded -> InboxLoaded((state.value as InboxState.Loaded).messageList)
        else -> Unit
    }
}

@Composable
private fun LoadingInbox() {
    Text(text = stringResource(id = R.string.inbox_loading_list))
}

@Composable
private fun EmptyInbox() {
    Text(text = stringResource(id = R.string.inbox_empty_list))
}

@Composable
private fun InboxLoaded(messageList: List<Message>) {
    LazyColumn {
        items(messageList) { message ->
            MessageCard(message)
        }
    }
}

@Composable
fun MessageCard(msg: Message) {
    Surface(shape = MaterialTheme.shapes.medium, elevation = 1.dp) {
        Column {
            Text(
                text = msg.address,
                color = MaterialTheme.colors.secondaryVariant,
                style = MaterialTheme.typography.subtitle2
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = msg.body,
                modifier = Modifier.padding(all = 4.dp),
                style = MaterialTheme.typography.body2
            )
        }
    }
}

