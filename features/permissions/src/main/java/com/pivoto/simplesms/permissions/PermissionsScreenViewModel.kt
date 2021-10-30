package com.pivoto.simplesms.permissions

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.pivoto.simplesms.permissions.nav.PermissionsScreenActions
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PermissionsScreenViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle
) :
    ViewModel() {

    private val _permissionState = mutableStateOf<PermissionState>(PermissionState.Idle)
    val permissionState: State<PermissionState> = _permissionState

    private val _defaultAppState = mutableStateOf<DefaultAppsState>(DefaultAppsState.Unknown)
    val defaultAppState: State<DefaultAppsState> = _defaultAppState

    lateinit var actionHandler: PermissionsScreenActions

    fun handlePermissionStateChange(newState: PermissionState) {
        when(newState) {
            PermissionState.Granted -> actionHandler.onPermissionGranted()
            PermissionState.Rejected -> _permissionState.value = PermissionState.Rejected
            else -> Unit
        }
    }

    fun handleDefaultAppStateChange(newState: DefaultAppsState) {
        when(newState) {
            DefaultAppsState.DefaultApp -> _permissionState.value = PermissionState.Unknown
            DefaultAppsState.NotDefaultApp -> _defaultAppState.value = DefaultAppsState.NotDefaultApp
            else -> Unit
        }
    }

    sealed class PermissionState {
        object Idle : PermissionState()
        object Unknown : PermissionState()
        object Granted : PermissionState()
        object Rejected : PermissionState()
    }

    sealed class DefaultAppsState {
        object Unknown : DefaultAppsState()
        object NotDefaultApp : DefaultAppsState()
        object DefaultApp : DefaultAppsState()
    }
}