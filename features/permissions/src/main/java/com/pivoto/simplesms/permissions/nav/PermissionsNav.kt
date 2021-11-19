package com.pivoto.simplesms.permissions.nav

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.pivoto.simplesms.permissions.PermissionsScreen
import javax.inject.Inject

class PermissionsNav @Inject constructor() {

    val destination = "Permissions"

    fun createGraph(navGraphBuilder: NavGraphBuilder, actionHandler: PermissionsScreenActions) {
        navGraphBuilder.composable(destination) {
            PermissionsScreen(actionHandler)
        }
    }
}

interface PermissionsScreenActions {

    fun onPermissionGranted()
}