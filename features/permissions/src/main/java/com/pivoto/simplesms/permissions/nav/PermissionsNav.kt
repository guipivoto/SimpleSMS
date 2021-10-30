package com.pivoto.simplesms.permissions.nav

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.pivoto.simplesms.permissions.PermissionsScreen
import com.pivoto.simplesms.permissions.PermissionsScreenViewModel
import javax.inject.Inject

class PermissionsNav @Inject constructor() {

    val destination = "Permissions"

    fun createGraph(navGraphBuilder: NavGraphBuilder, actionHandler: PermissionsScreenActions) {
        navGraphBuilder.composable(destination) {
            val viewModel = hiltViewModel<PermissionsScreenViewModel>()
            viewModel.actionHandler = actionHandler
            PermissionsScreen(viewModel)
        }
    }
}

interface PermissionsScreenActions {

    fun onPermissionGranted()
}