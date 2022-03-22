package com.pivoto.simplesms.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.pivoto.simplesms.conversation.nav.ConversationNav
import com.pivoto.simplesms.inbox.nav.InboxNav
import com.pivoto.simplesms.permissions.nav.PermissionsNav
import com.pivoto.simplesms.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity: ComponentActivity() {

    @Inject
    lateinit var permissionsModule: PermissionsNav

    @Inject
    lateinit var inboxModule: InboxNav

    @Inject
    lateinit var conversationModule: ConversationNav

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                MainScreen()
            }

        }
    }

    @Composable
    fun MainScreen() {
        SimpleSMSNavGraph(permissionsModule, inboxModule, conversationModule)
    }
}