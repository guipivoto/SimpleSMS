package com.pivoto.simplesms.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.pivoto.simplesms.conversation.nav.ConversationNav
import com.pivoto.simplesms.conversation.nav.ConversationScreenEvents
import com.pivoto.simplesms.inbox.nav.InboxNav
import com.pivoto.simplesms.inbox.nav.InboxScreenEvents
import com.pivoto.simplesms.permissions.nav.PermissionsNav
import com.pivoto.simplesms.permissions.nav.PermissionsScreenEvents

@Composable
fun SimpleSMSNavGraph(
    permissionsNav: PermissionsNav,
    inboxNav: InboxNav,
    conversationNav: ConversationNav,
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = permissionsNav.destination) {
        permissionsNav.createGraph(this, actionHandler = object : PermissionsScreenEvents {
            override fun onPermissionGranted() {
                navController.popBackStack()
                navController.navigate(inboxNav.destination)
            }
        })
        inboxNav.createGraph(this, actionHandler = object : InboxScreenEvents {
            override fun onConversationOpened(messageAddress: String) {
                val route = conversationNav.destination
                navController.navigate(route.plus("/$messageAddress"))
            }
        })
        conversationNav.createGraph(this, object : ConversationScreenEvents {})
    }
}