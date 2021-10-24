package com.pivoto.simplesms.inbox.nav

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.pivoto.simplesms.inbox.InboxScreen
import com.pivoto.simplesms.inbox.InboxScreenViewModel
import javax.inject.Inject

class InboxNav @Inject constructor() {

    val destination = "Inbox"

    fun createGraph(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable(destination) {
            val viewModel = hiltViewModel<InboxScreenViewModel>()
            InboxScreen(viewModel)
        }
    }
}