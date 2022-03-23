package com.pivoto.simplesms.inbox.nav

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.pivoto.simplesms.inbox.InboxScreen
import com.pivoto.simplesms.inbox.InboxScreenViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

internal class InboxNavImpl @Inject constructor() : InboxNav {

    override val destination = "inbox"

    override fun createGraph(navGraphBuilder: NavGraphBuilder, actionHandler: InboxScreenEvents) {
        navGraphBuilder.composable(destination) {
            val viewModel = hiltViewModel<InboxScreenViewModel>()
            InboxScreen(viewModel, actionHandler)
        }
    }
}

@Module
@InstallIn(ActivityComponent::class)
internal abstract class InboxNavModule {

    @ActivityScoped
    @Binds
    abstract fun provideInboxNavModule(implementation : InboxNavImpl) : InboxNav
}