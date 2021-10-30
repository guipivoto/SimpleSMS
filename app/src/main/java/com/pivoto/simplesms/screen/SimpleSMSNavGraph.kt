package com.pivoto.simplesms.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.pivoto.simplesms.inbox.nav.InboxNav
import com.pivoto.simplesms.permissions.nav.PermissionsScreenActions
import com.pivoto.simplesms.permissions.nav.PermissionsNav

@Composable
fun SimpleSMSNavGraph(
    permissionsModule: PermissionsNav,
    inboxNav: InboxNav,
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = permissionsModule.destination) {
        permissionsModule.createGraph(this, actionHandler = object : PermissionsScreenActions {
            override fun onPermissionGranted() {
                navController.popBackStack()
                navController.navigate(inboxNav.destination)
            }
        })
        inboxNav.createGraph(this)
    }
}