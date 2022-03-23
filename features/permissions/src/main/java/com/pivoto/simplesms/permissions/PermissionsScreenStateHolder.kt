package com.pivoto.simplesms.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.provider.Telephony
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.pivoto.simplesms.permissions.nav.PermissionsScreenEvents

/**
 * This a simple screen state holder to support the UI
 * Since there's no business logic involved, this state holder don't need to evolve into a ViewHolder
 */
class PermissionsScreenStateHolder(
    /*private val */
    context: Context,
    private val actionHandler: PermissionsScreenEvents,
) {
    private var _permissionState = mutableStateOf<PermissionState>(PermissionState.Idle)
    val permissionState = _permissionState

    private var _defaultAppState = mutableStateOf<DefaultAppsState>(DefaultAppsState.Idle)
    val defaultAppState = _defaultAppState

    private val doNotAskAgain by lazy {
        !ActivityCompat.shouldShowRequestPermissionRationale(
            context.findActivity(),
            SMS_PERMISSION
        )
    }

    init {
        val packageName = context.packageName

        if (packageName.equals(Telephony.Sms.getDefaultSmsPackage(context))) {
            _defaultAppState.value = DefaultAppsState.DefaultApp


            val permissionGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_SMS
            ) == PackageManager.PERMISSION_GRANTED

            if (permissionGranted) {
                actionHandler.onPermissionGranted()
            } else {
                _permissionState.value = PermissionState.Pending
            }
        } else {
            _defaultAppState.value = DefaultAppsState.NotDefaultApp
        }
    }

    fun handleDefaultAppStateChange(newState: DefaultAppsState) {
        when (newState) {
            is DefaultAppsState.DefaultApp -> {
                _defaultAppState.value = DefaultAppsState.DefaultApp
                _permissionState.value = PermissionState.Pending
            }
            is DefaultAppsState.DefaultAppRejected -> {
                _defaultAppState.value = DefaultAppsState.DefaultAppRejected
            }
            else -> Unit
        }
    }

    fun handlePermissionStateChange(newState: PermissionState) {
        when (newState) {
            is PermissionState.Granted -> actionHandler.onPermissionGranted()
            is PermissionState.Rejected -> {
                _permissionState.value = if (doNotAskAgain) {
                    PermissionState.PermanentRejected
                } else {
                    PermissionState.Rejected
                }
            }
            else -> Unit
        }
    }

    private fun Context.findActivity(): Activity {
        var context = this
        while (context is ContextWrapper) {
            if (context is Activity) return context
            context = context.baseContext
        }
        throw IllegalStateException("Permissions should be called in the context of an Activity")
    }

    sealed class PermissionState {
        object Idle : PermissionState()
        object Pending : PermissionState()
        object Granted : PermissionState()
        object Rejected : PermissionState()
        object PermanentRejected : PermissionState()
    }

    sealed class DefaultAppsState {
        object Idle : DefaultAppsState()
        object NotDefaultApp : DefaultAppsState()
        object DefaultApp : DefaultAppsState()
        object DefaultAppRejected : DefaultAppsState()
    }

    companion object {
        const val SMS_PERMISSION = Manifest.permission.READ_SMS
    }
}