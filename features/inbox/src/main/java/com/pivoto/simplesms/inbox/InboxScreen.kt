package com.pivoto.simplesms.inbox

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun InboxScreen(viewModel: InboxScreenViewModel) {

    viewModel.messageList.value?.also {
        Text(text = it)
    }
}

