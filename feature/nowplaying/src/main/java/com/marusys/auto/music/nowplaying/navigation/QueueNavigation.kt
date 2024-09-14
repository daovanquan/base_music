package com.marusys.auto.music.nowplaying.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.marusys.auto.music.store.model.album.BasicAlbum

const val QUEUE_NAVIGATION_GRAPH = "queue_graph"
const val QUEUE_ROUTE = "queue_route"

fun NavController.navigateToQueue(navOptions: NavOptions? = null) {
    navigate(QUEUE_ROUTE, navOptions)
}

fun NavGraphBuilder.queueGraph(
    queueScreen: @Composable () -> Unit
) {

    navigation(
        route = QUEUE_NAVIGATION_GRAPH,
        startDestination = QUEUE_ROUTE,
    ) {
        composable(
            QUEUE_ROUTE
        )
        {
            queueScreen()
        }
    }

}