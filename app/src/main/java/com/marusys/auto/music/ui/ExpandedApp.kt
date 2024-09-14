package com.marusys.auto.music.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import com.marusys.auto.music.navigation.MusicaNavigationRail
import com.marusys.auto.music.navigation.TopLevelDestination
import com.marusys.auto.music.nowplaying.NowPlayingState
import com.marusys.auto.music.state.MusicaAppState
import com.marusys.auto.music.nowplaying.ui.BarState
import com.marusys.auto.music.nowplaying.ui.NowPlayingScreen
import com.marusys.auto.music.nowplaying.viewmodel.NowPlayingViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt


private val EXPANDED_SCREEN_NOW_PLAYING_HEIGHT = 56.dp

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ExpandedAppScaffold(
    modifier: Modifier,
    appState: MusicaAppState,
    nowPlayingScreenAnchors: AnchoredDraggableState<BarState>,
    topLevelDestinations: List<TopLevelDestination>,
    currentDestination: NavDestination?,
    onDestinationSelected: (TopLevelDestination) -> Unit,
    content: @Composable (Modifier, MutableState<Modifier>) -> Unit,
) {

    var nowPlayingMinOffset by remember { mutableIntStateOf(1) }
    val density = LocalDensity.current

    // Navhost takes the whole available screen.
    // contentModifier is added to the screens (composable) themselves to handle cases
    // such as when NowPlayingBar is hidden or visible
    val contentModifier = remember {
        mutableStateOf<Modifier>(Modifier)
    }

    val uiState by appState.nowPlayingViewModel.state.collectAsState()


    val navigationBarInsets = WindowInsets.navigationBars

    var screenHeightPx by remember {
        mutableIntStateOf(0)
    }

    Box(modifier = modifier.onGloballyPositioned { screenHeightPx = it.size.height }) {
        Row(modifier = Modifier.fillMaxSize()) {
            Row(Modifier.weight(1f).fillMaxHeight()) {
                content(
                    Modifier
                        .fillMaxSize()
                        .consumeRailInsets(LocalLayoutDirection.current, density, WindowInsets.navigationBars),
                    contentModifier,
                )
            }
            AnimatedVisibility(visible = uiState is NowPlayingState.Playing,
                modifier = Modifier.weight(1f, fill = false).fillMaxHeight(),
                label = "NowPlaying"
            ) {
                if(uiState is NowPlayingState.Playing)
                NowPlayingScreen(
                    modifier = Modifier.fillMaxSize()
                        .offset {
                            IntOffset(
                                x = 0,
                                y = 0,
                            )
                        }
                        .onSizeChanged {
                            /*nowPlayingMinOffset = nowPlayingScreenAnchors.update(
                                it.height,
                                with(density) { EXPANDED_SCREEN_NOW_PLAYING_HEIGHT.toPx() }.toInt(),
                                0
                            )*/
                        },
                    nowPlayingBarPadding =
                    PaddingValues(
                        end = with(density) {
                            navigationBarInsets.getRight(this, LayoutDirection.Ltr).toDp()
                        },
                        start = with(density) {
                            navigationBarInsets.getLeft(this, LayoutDirection.Ltr).toDp()
                        }
                    ),
                    barHeight = EXPANDED_SCREEN_NOW_PLAYING_HEIGHT,
                    isExpanded = true,
                    onCollapseNowPlaying = {
                        appState.coroutineScope.launch {
                            nowPlayingScreenAnchors.animateTo(
                                BarState.COLLAPSED
                            )
                        }
                    },
                    onExpandNowPlaying = {
                        appState.coroutineScope.launch {
                            nowPlayingScreenAnchors.animateTo(
                                BarState.EXPANDED
                            )
                        }
                    },
                    progressProvider = { 1 - (appState.nowPlayingScreenOffset() / nowPlayingMinOffset) },
                    viewModel = appState.nowPlayingViewModel
                )
            }
        }
    }
}

fun Modifier.consumeRailInsets(
    layoutDirection: LayoutDirection,
    density: Density,
    navigationBarsInsets: WindowInsets
): Modifier =
    this.consumeWindowInsets(
        PaddingValues(start = if (layoutDirection == LayoutDirection.Ltr)
            with(density) {
                navigationBarsInsets
                    .getLeft(
                        density,
                        layoutDirection
                    )
                    .toDp()
            }
        else
            with(density) {
                navigationBarsInsets
                    .getRight(
                        density,
                        layoutDirection
                    )
                    .toDp()
            }
        )
    ) // consume the insets handled by the Rail
