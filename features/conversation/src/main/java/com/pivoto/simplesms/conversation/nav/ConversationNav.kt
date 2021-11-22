package com.pivoto.simplesms.conversation.nav

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.pivoto.simplesms.conversation.ConversationScreen
import com.pivoto.simplesms.conversation.ConversationScreenViewModel
import javax.inject.Inject

class ConversationNav @Inject constructor() {

    val destination = "Conversation"
    private val arguments = "/{address}"

    fun createGraph(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable(destination.plus(arguments)) {
            val viewModel = hiltViewModel<ConversationScreenViewModel>()
            ConversationScreen(viewModel)
        }
    }
}