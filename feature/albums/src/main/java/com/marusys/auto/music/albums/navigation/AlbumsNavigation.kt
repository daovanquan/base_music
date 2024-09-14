package com.marusys.auto.music.albums.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.marusys.auto.music.albums.ui.albumdetail.AlbumDetailsScreen
import com.marusys.auto.music.albums.ui.albumsscreen.AlbumsScreen
import com.marusys.auto.music.albums.viewmodel.AlbumDetailsViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


const val ALBUMS_NAVIGATION_GRAPH = "albums_graph"
const val ALBUMS_ROUTE = "albums"
const val ALBUM_DETAIL_ROUTE = "album/{${AlbumDetailsViewModel.ALBUM_ID_KEY}}"


fun NavController.navigateToAlbums(navOptions: NavOptions? = null) {
    navigate(ALBUMS_NAVIGATION_GRAPH, navOptions)
}

fun NavController.navigateToAlbumDetail(albumId: Int, navOptions: NavOptions? = null) {
    navigate("album/$albumId", navOptions)
}

fun NavGraphBuilder.albumsGraph(
    contentModifier: MutableState<Modifier>,
    navController: NavController,
    enableBackPress: MutableState<Boolean>,
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
        route = ALBUMS_NAVIGATION_GRAPH,
        startDestination = ALBUMS_ROUTE,
    ) {
        composable(
            ALBUMS_ROUTE,
            enterTransition = {
                enterAnimationFactory(ALBUMS_ROUTE, this)
            },
            exitTransition = {
                exitAnimationFactory(ALBUMS_ROUTE, this)
            },
            popEnterTransition = {
                popEnterAnimationFactory(ALBUMS_ROUTE, this)
            },
            popExitTransition = {
                popExitAnimationFactory(ALBUMS_ROUTE, this)
            }
        ) {
            AlbumsScreen(
                modifier = contentModifier.value,
                onAlbumClicked = { albumId -> navController.navigateToAlbumDetail(albumId) },
                onClose = { navController.popBackStack() },
            )
        }

        composable(
            ALBUM_DETAIL_ROUTE,
            enterTransition = {
                enterAnimationFactory(ALBUM_DETAIL_ROUTE, this)
            },
            popExitTransition = {
                popExitAnimationFactory(ALBUM_DETAIL_ROUTE, this)
            },
            arguments = listOf(
                navArgument(AlbumDetailsViewModel.ALBUM_ID_KEY) {
                    type = NavType.IntType
                }
            )
        )
        { entry ->
            AlbumDetailsScreen(
                modifier = contentModifier.value,
                onBackClicked = { navController.popBackStack() },
                viewModel = koinViewModel(parameters = { parametersOf(entry.arguments?.getInt(AlbumDetailsViewModel.ALBUM_ID_KEY)) }),
                onNavigateToAlbum = { albumId -> navController.navigateToAlbumDetail(albumId) }
            )
        }
    }

}