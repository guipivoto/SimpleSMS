package com.pivoto.simplesms.screen

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.pivoto.simplesms.inbox.nav.InboxNav

@Composable
fun SimpleSMSNavGraph(
    inboxNav: InboxNav
) {
    NavHost(navController = rememberNavController(), startDestination = inboxNav.destination) {
        inboxNav.createGraph(this)
    }
}