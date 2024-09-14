package com.marusys.auto.music.ui

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.util.Consumer
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.navigation.navigation
import com.marusys.auto.music.albums.navigation.albumsGraph
import com.marusys.auto.music.albums.navigation.navigateToAlbumDetail
import com.marusys.auto.music.navigation.MainMenuScreen
import com.marusys.auto.music.navigation.TopLevelDestination
import com.marusys.auto.music.navigation.navigateToTopLevelDestination
import com.marusys.auto.music.nowplaying.NowPlayingState
import com.marusys.auto.music.nowplaying.navigation.QUEUE_ROUTE
import com.marusys.auto.music.nowplaying.navigation.navigateToQueue
import com.marusys.auto.music.nowplaying.navigation.queueGraph
import com.marusys.auto.music.nowplaying.queue.QueueScreen
import com.marusys.auto.music.playback.PlaybackService
import com.marusys.auto.music.playlists.navigation.playlistsGraph
import com.marusys.auto.music.settings.navigation.settingsGraph
import com.marusys.auto.music.state.rememberMusicaAppState
import com.marusys.auto.music.tageditor.navigation.tagEditorGraph
import com.marusys.auto.music.nowplaying.ui.BarState
import com.marusys.auto.music.songs.navigation.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber


val topLevelDestinations =
    listOf(
        TopLevelDestination.SONGS,
        TopLevelDestination.PLAYLISTS,
        TopLevelDestination.ALBUMS,
        TopLevelDestination.SETTINGS
    )


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3AdaptiveApi::class
)
@Composable
fun MusicaApp2(
    modifier: Modifier,
    navController: NavHostController
) {


    val widthClass = calculateWindowSizeClass(activity = LocalContext.current as Activity)

    val density = LocalDensity.current
    val nowPlayingScreenAnchors = remember {
        AnchoredDraggableState(
            BarState.COLLAPSED,
            anchors = DraggableAnchors {
                BarState.COLLAPSED at 0.0f
                BarState.EXPANDED at 0.0f
            },
            { distance: Float -> 0.5f * distance },
            { with(density) { 70.dp.toPx() } },
            tween(),
            exponentialDecay()
        )
    }

    val appState = rememberMusicaAppState(
        navHostController = navController,
        isNowPlayingExpanded = nowPlayingScreenAnchors.currentValue == BarState.EXPANDED,
        nowPlayingViewModel = koinViewModel(),
        nowPlayingScreenOffset = {
            if (nowPlayingScreenAnchors.anchors.size > 0)
                nowPlayingScreenAnchors.requireOffset()
            else 0.0f
        },
    )

    val showQueue: Boolean by appState.nowPlayingViewModel.showQueue.collectAsState()
    val playingState: NowPlayingState by appState.nowPlayingViewModel.state.collectAsState()

    val navHost = remember {
        movableContentOf<Modifier, MutableState<Modifier>> { navHostModifier, contentModifier ->
            NavHost(
                modifier = navHostModifier,
                navController = appState.navHostController,
                startDestination = MAIN_NAVIGATION_GRAPH
            ) {
                navigation(route = MAIN_NAVIGATION_GRAPH,
                    startDestination = MAIN_MENU_ROUTE,) {

                    composable(MAIN_MENU_ROUTE) {
                        MainMenuScreen(
                            modifier = contentModifier.value,
                            topLevelDestinations = topLevelDestinations,
                            onSearchClicked = {
                                navController.navigateToSearch()
                            },
                            nowPlayingState = playingState,
                            currentDestination = navController.currentBackStackEntryAsState().value?.destination,
                            onDestinationSelected = { navController.navigateToTopLevelDestination(it) }
                        )
                    }

                    songsGraph(
                        contentModifier = contentModifier,
                        navController,
                        enableBackPress = mutableStateOf(false),
                        onNavigateToAlbum = {
                            navController.navigateToAlbumDetail(it.albumInfo.id)
                        },
                        enterAnimationFactory = ::getEnterAnimationForRoute,
                        exitAnimationFactory = ::getExitAnimationForRoute,
                        popEnterAnimationFactory = ::getPopEnterAnimationForRoute,
                        popExitAnimationFactory = ::getPopExitAnimationForRoute
                    )
                    playlistsGraph(
                        contentModifier = contentModifier,
                        navController,
                        enterAnimationFactory = ::getEnterAnimationForRoute,
                        exitAnimationFactory = ::getExitAnimationForRoute,
                        popEnterAnimationFactory = ::getPopEnterAnimationForRoute,
                        popExitAnimationFactory = ::getPopExitAnimationForRoute
                    )
                    albumsGraph(
                        contentModifier = contentModifier,
                        navController,
                        enableBackPress = mutableStateOf(false),
                        enterAnimationFactory = ::getEnterAnimationForRoute,
                        exitAnimationFactory = ::getExitAnimationForRoute,
                        popEnterAnimationFactory = ::getPopEnterAnimationForRoute,
                        popExitAnimationFactory = ::getPopExitAnimationForRoute
                    )
                    settingsGraph(
                        contentModifier = contentModifier,
                        navController,
                        enterAnimationFactory = ::getEnterAnimationForRoute,
                        exitAnimationFactory = ::getExitAnimationForRoute,
                        popEnterAnimationFactory = ::getPopEnterAnimationForRoute,
                        popExitAnimationFactory = ::getPopExitAnimationForRoute
                    )
                    tagEditorGraph(
                        contentModifier = contentModifier,
                        navController,
                        enterAnimationFactory = ::getEnterAnimationForRoute,
                        exitAnimationFactory = ::getExitAnimationForRoute,
                        popEnterAnimationFactory = ::getPopEnterAnimationForRoute,
                        popExitAnimationFactory = ::getPopExitAnimationForRoute
                    )

                }
                composable(QUEUE_ROUTE) {
                    QueueScreen(
                        modifier = Modifier
                            .fillMaxSize(),
                        onClose = {
                            navController.popBackStack()
                        },
                        onClearQueue = {
                            navController.popBackStack()
                        }
                    )
                }

            }
        }
    }

    LaunchedEffect(showQueue) {
        if (playingState is NowPlayingState.Playing) {
            navController.navigate(QUEUE_ROUTE, navOptions {
               launchSingleTop = true
            })
        }
    }

    ExpandedAppScaffold(
        modifier = modifier,
        appState = appState,
        nowPlayingScreenAnchors = nowPlayingScreenAnchors,
        topLevelDestinations = topLevelDestinations,
        currentDestination = navController.currentBackStackEntryAsState().value?.destination,
        onDestinationSelected = { navController.navigateToTopLevelDestination(it) }
    ) { navHostModifier, contentModifier ->
        navHost(navHostModifier, contentModifier)
    }

    ViewNowPlayingScreenListenerEffect(
        navController = navController,
        onViewNowPlayingScreen = {
            appState.coroutineScope.launch {
                nowPlayingScreenAnchors.animateTo(
                    BarState.EXPANDED
                )
            }
        }
    )

}

/**
 * This is responsible to collapse the NowPlayingScreen
 * when a navigation event happens
 */
@Composable
fun NowPlayingCollapser(
    navController: NavHostController,
    onCollapse: suspend () -> Unit
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(key1 = currentBackStackEntry) {
        onCollapse()
    }
}


/**
 * Responsible to expand the NowPlayingScreen when an intent is received
 * or when the app is launched from the media notification
 */
@Composable
fun ViewNowPlayingScreenListenerEffect(
    navController: NavController,
    onViewNowPlayingScreen: () -> Unit
) {
    val context = LocalContext.current
    var handledIntent by rememberSaveable {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = Unit) {
        delay(500)
        val activity = (context as? Activity) ?: return@LaunchedEffect
        val action = activity.intent.action
        if (action == PlaybackService.VIEW_MEDIA_SCREEN_ACTION && !handledIntent) {
            onViewNowPlayingScreen()
            handledIntent = true
        }
    }

    DisposableEffect(navController) {
        val listener = Consumer<Intent> {
            if (it.action == PlaybackService.VIEW_MEDIA_SCREEN_ACTION) {
                onViewNowPlayingScreen()
            }
        }
        val activity = (context as? ComponentActivity) ?: return@DisposableEffect onDispose { }
        activity.addOnNewIntentListener(listener)
        onDispose { activity.removeOnNewIntentListener(listener) }
    }
}


@OptIn(ExperimentalFoundationApi::class)
fun AnchoredDraggableState<BarState>.update(
    layoutHeightPx: Int,
    barHeightPx: Int,
    bottomBarHeightPx: Int
): Int {
    var offset = 0
    updateAnchors(
        DraggableAnchors {
            offset =
                (-barHeightPx + layoutHeightPx - bottomBarHeightPx)
            BarState.COLLAPSED at offset.toFloat()
            BarState.EXPANDED at 0.0f
        },
        this.currentValue
    )
    return offset
}

fun calculateBottomPaddingForContent(
    shouldShowNowPlayingBar: Boolean,
    bottomBarHeight: Dp,
    nowPlayingBarHeight: Dp
): Dp {
    return bottomBarHeight + (if (shouldShowNowPlayingBar) nowPlayingBarHeight else 0.dp)
}