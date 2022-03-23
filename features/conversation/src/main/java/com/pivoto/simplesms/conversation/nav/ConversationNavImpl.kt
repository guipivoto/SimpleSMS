package com.pivoto.simplesms.conversation.nav

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.pivoto.simplesms.conversation.ConversationScreen
import com.pivoto.simplesms.conversation.ConversationScreenViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

internal class ConversationNavImpl @Inject constructor() : ConversationNav {

    override val destination = "Conversation"
    private val arguments = "/{address}"

    override fun createGraph(navGraphBuilder: NavGraphBuilder, actionHandler : ConversationScreenEvents) {
        navGraphBuilder.composable(destination.plus(arguments)) {
            val viewModel = hiltViewModel<ConversationScreenViewModel>()
            ConversationScreen(viewModel)
        }
    }
}

@Module
@InstallIn(ActivityComponent::class)
internal abstract class ConversationNavModule {

    @ActivityScoped
    @Binds
    abstract fun providesConversationNavModule(implementation : ConversationNavImpl) : ConversationNav
}