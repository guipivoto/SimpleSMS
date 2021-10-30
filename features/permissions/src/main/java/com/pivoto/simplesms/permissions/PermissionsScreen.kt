package com.pivoto.simplesms.permissions

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Telephony.Sms
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun PermissionsScreen(viewModel: PermissionsScreenViewModel) {
    PermissionScreenContent(viewModel.permissionState) {
         viewModel.handlePermissionStateChange(newState = it)
    }
    DefaultAppScreenContent(viewModel.defaultAppState) {
         viewModel.handleDefaultAppStateChange(newState = it)
    }
}

@Composable
private fun DefaultAppScreenContent(
    defaultAppState: State<PermissionsScreenViewModel.DefaultAppsState>,
    stateChanged: (PermissionsScreenViewModel.DefaultAppsState) -> Unit
) {
    when (defaultAppState.value) {
        is PermissionsScreenViewModel.DefaultAppsState.Unknown -> RequestSetDefaultSmsApp(stateChanged)
        is PermissionsScreenViewModel.DefaultAppsState.NotDefaultApp -> NotifyNotDefaultApp()
        else -> Unit
    }
}

@Composable
private fun PermissionScreenContent(
    permissionState: State<PermissionsScreenViewModel.PermissionState>,
    stateChanged: (PermissionsScreenViewModel.PermissionState) -> Unit
) {
    when (permissionState.value) {
        is PermissionsScreenViewModel.PermissionState.Unknown -> RequestPermission(stateChanged)
        is PermissionsScreenViewModel.PermissionState.Rejected -> NotifyNoPermissionGranted()
        else -> Unit
    }
}

@Composable
private fun NotifyNotDefaultApp() {
    // TODO
}

@Composable
private fun NotifyNoPermissionGranted() {
    // TODO
}

@Composable
private fun RequestSetDefaultSmsApp(stateChanged: (PermissionsScreenViewModel.DefaultAppsState) -> Unit) {
    val context = LocalContext.current
    val packageName = context.packageName
    if(packageName.equals(Sms.getDefaultSmsPackage(context))) {
        stateChanged(PermissionsScreenViewModel.DefaultAppsState.DefaultApp)
    } else {
        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (packageName.equals(Sms.getDefaultSmsPackage(context))) {
                stateChanged(PermissionsScreenViewModel.DefaultAppsState.DefaultApp)
            } else {
                stateChanged(PermissionsScreenViewModel.DefaultAppsState.NotDefaultApp)
            }
        }
        SideEffect {
            val intent = Intent(Sms.Intents.ACTION_CHANGE_DEFAULT)
            intent.putExtra(Sms.Intents.EXTRA_PACKAGE_NAME, packageName)
            launcher.launch(intent)
        }
    }
}

@Composable
private fun RequestPermission(stateChanged: (PermissionsScreenViewModel.PermissionState) -> Unit) {

    val isGranted = ContextCompat.checkSelfPermission(
        LocalContext.current, Manifest.permission.READ_SMS
    ) == PackageManager.PERMISSION_GRANTED

    if (isGranted) {
        stateChanged(PermissionsScreenViewModel.PermissionState.Granted)
    } else {
        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if (it) {
                stateChanged(PermissionsScreenViewModel.PermissionState.Granted)
            } else {
                stateChanged(PermissionsScreenViewModel.PermissionState.Rejected)
            }
        }
        SideEffect {
            launcher.launch(Manifest.permission.READ_SMS)
        }

    }
}