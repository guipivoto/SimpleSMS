package com.pivoto.simplesms.permissions.nav

import com.pivoto.simplesms.contract.Feature
import com.pivoto.simplesms.contract.FeatureEvents

sealed interface PermissionsNav : Feature<PermissionsScreenEvents>

interface PermissionsScreenEvents : FeatureEvents {

    fun onPermissionGranted()
}