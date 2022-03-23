package com.pivoto.simplesms.permissions.nav

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.pivoto.simplesms.permissions.PermissionsScreen
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

internal class PermissionsNavImpl @Inject constructor() : PermissionsNav {

    override val destination = "permissions"

    override fun createGraph(navGraphBuilder: NavGraphBuilder, actionHandler: PermissionsScreenEvents) {
        navGraphBuilder.composable(destination) {
            PermissionsScreen(actionHandler)
        }
    }
}

@Module
@InstallIn(ActivityComponent::class)
internal abstract class PermissionsNavModule {

    @ActivityScoped
    @Binds
    abstract fun providePermissionNavModule(implementation : PermissionsNavImpl) : PermissionsNav
}