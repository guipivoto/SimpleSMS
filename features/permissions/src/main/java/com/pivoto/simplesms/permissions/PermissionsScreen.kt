package com.pivoto.simplesms.permissions

import android.Manifest
import android.content.Intent
import android.provider.Telephony.Sms
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.pivoto.simplesms.permissions.nav.PermissionsScreenActions

@Composable
fun PermissionsScreen(actionHandler: PermissionsScreenActions) {
    val context = LocalContext.current
    val permissionScreenState = remember {
        PermissionsScreenStateHolder(context, actionHandler)
    }

    DefaultAppScreenContent(defaultAppState = permissionScreenState) {
        permissionScreenState.handleDefaultAppStateChange(
            newState = it
        )
    }

    PermissionScreenContent(permissionState = permissionScreenState) {
        permissionScreenState.handlePermissionStateChange(
            newState = it
        )
    }
}

@Composable
private fun DefaultAppScreenContent(
    defaultAppState: PermissionsScreenStateHolder,
    stateChanged: (PermissionsScreenStateHolder.DefaultAppsState) -> Unit
) {
    when (defaultAppState.defaultAppState.value) {
        is PermissionsScreenStateHolder.DefaultAppsState.NotDefaultApp -> {
            val context = LocalContext.current
            val packageName = context.packageName

            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                if (packageName.equals(Sms.getDefaultSmsPackage(context))) {
                    stateChanged(PermissionsScreenStateHolder.DefaultAppsState.DefaultApp)
                } else {
                    stateChanged(PermissionsScreenStateHolder.DefaultAppsState.DefaultAppRejected)
                }
            }
            SideEffect {
                val intent = Intent(Sms.Intents.ACTION_CHANGE_DEFAULT)
                intent.putExtra(Sms.Intents.EXTRA_PACKAGE_NAME, packageName)
                launcher.launch(intent)
            }
        }
        is PermissionsScreenStateHolder.DefaultAppsState.DefaultAppRejected -> NotifyNotDefaultApp()
        else -> Unit
    }
}

@Composable
private fun PermissionScreenContent(
    permissionState: PermissionsScreenStateHolder,
    stateChanged: (PermissionsScreenStateHolder.PermissionState) -> Unit
) {
    when (permissionState.permissionState.value) {
        is PermissionsScreenStateHolder.PermissionState.Pending -> RequestPermission(
            stateChanged
        )
        is PermissionsScreenStateHolder.PermissionState.Rejected -> NotifyNoPermissionGranted()
        is PermissionsScreenStateHolder.PermissionState.PermanentRejected -> NotifyPermissionRationale()
        else -> Unit
    }
}

@Composable
private fun RequestPermission(stateChanged: (PermissionsScreenStateHolder.PermissionState) -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            stateChanged(PermissionsScreenStateHolder.PermissionState.Granted)
        } else {
            stateChanged(PermissionsScreenStateHolder.PermissionState.Rejected)
        }
    }
    SideEffect {
        launcher.launch(Manifest.permission.READ_SMS)
    }
}

@Composable
private fun NotifyNotDefaultApp() {
    Text(text = stringResource(id = R.string.permissions_not_default_app))
}

@Composable
private fun NotifyNoPermissionGranted() {
    Text(text = stringResource(id = R.string.permissions_not_granted))
}

@Composable
private fun NotifyPermissionRationale() {
    Text(text = stringResource(id = R.string.permissions_not_granted_rationale))
}
