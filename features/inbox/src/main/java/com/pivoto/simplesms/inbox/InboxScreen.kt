package com.pivoto.simplesms.inbox

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.stringResource
import com.pivoto.simplesms.message.Message

@Composable
fun InboxScreen(viewModel: InboxScreenViewModel) {
    var inboxState = viewModel.inboxState.observeAsState()
    InboxContent(state = inboxState)
}

@Composable
fun InboxContent(state: State<InboxState?>) {
    when(state.value) {
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
    Text(text = messageList[0].text)
}
