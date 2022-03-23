package com.pivoto.simplesms.contract

import androidx.navigation.NavGraphBuilder

sealed interface BaseFeatures

sealed interface BaseFeatureEvents

interface Feature<in T : FeatureEvents> : BaseFeatures {

    val destination : String

    fun createGraph(navGraphBuilder: NavGraphBuilder, actionHandler: T)
}

interface FeatureEvents : BaseFeatureEvents