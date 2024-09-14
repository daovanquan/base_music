package com.marusys.auto.music.settings.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.marusys.auto.music.settings.SettingsScreen


const val SETTINGS_NAVIGATION_GRAPH = "settings_graph"
const val SETTINGS_ROUTE = "settings_route"


fun NavGraphBuilder.settingsGraph(
    contentModifier: MutableState<Modifier>,
    navController: NavController,
    enterAnimationFactory:
        (String, AnimatedContentTransitionScope<NavBackStackEntry>) -> EnterTransition,
    exitAnimationFactory:
        (String, AnimatedContentTransitionScope<NavBackStackEntry>) -> ExitTransition,
    popEnterAnimationFactory:
        (String, AnimatedContentTransitionScope<NavBackStackEntry>) -> EnterTransition,
    popExitAnimationFactory:
        (String, AnimatedContentTransitionScope<NavBackStackEntry>) -> ExitTransition,
) {

    navigation(
        route = SETTINGS_NAVIGATION_GRAPH,
        startDestination = SETTINGS_ROUTE
    ) {
        composable(
            SETTINGS_ROUTE,
            enterTransition = {
                enterAnimationFactory(SETTINGS_ROUTE, this)
            },
            exitTransition = {
                exitAnimationFactory(SETTINGS_ROUTE, this)
            },
            popEnterTransition = {
                popEnterAnimationFactory(SETTINGS_ROUTE, this)
            },
            popExitTransition = {
                popExitAnimationFactory(SETTINGS_ROUTE, this)
            }
        ) {
            SettingsScreen(modifier = contentModifier.value, onClose = {
                navController.popBackStack()
            })
        }
    }

}