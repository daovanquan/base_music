package com.marusys.auto.music

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.marusys.auto.music.actions.RealOpenTagEditorAction
import com.marusys.auto.music.playback.PlaybackManager
import com.marusys.auto.music.store.repository.MediaRepository
import com.marusys.auto.music.store.repository.UserPreferencesRepository
import com.marusys.auto.music.ui.AskPermissionScreen
import com.marusys.auto.music.ui.MusicaApp2
import com.marusys.auto.music.ui.common.LocalCommonSongsAction
import com.marusys.auto.music.ui.common.LocalUserPreferences
import com.marusys.auto.music.ui.common.rememberCommonSongsActions
import com.marusys.auto.music.ui.model.toUiModel
import com.marusys.auto.music.ui.theme.MusicaTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityScope
import org.koin.core.scope.Scope

class MainActivity : ComponentActivity(), AndroidScopeComponent {

    override val scope: Scope by activityScope()

    override fun onCloseScope() {
        // Called before closing the Scope
    }

    private val userPreferencesRepository: UserPreferencesRepository by inject()

    private val playbackManager: PlaybackManager by inject()

    private val mediaRepository: MediaRepository by inject()

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val initialUserPreferences =
            runBlocking { userPreferencesRepository.userSettingsFlow.first().toUiModel() }

        val userPreferencesFlow = userPreferencesRepository.userSettingsFlow.map { it.toUiModel() }

        setContent {

            val userPreferences by userPreferencesFlow
                .collectAsState(
                    initial = initialUserPreferences
                )

            val usbId = remember {
                derivedStateOf {
                    userPreferences.librarySettings.usbIdConnected
                }
            }

            val navController = rememberNavController()

            MusicaTheme(
                userPreferences = userPreferences,
            ) {
                val commonSongsActions =
                    rememberCommonSongsActions(
                        playbackManager,
                        mediaRepository,
                        remember { RealOpenTagEditorAction(navController) }
                    )


                val permissionName = getReadingMediaPermissionName()
                val storagePermissionState =
                    rememberPermissionState(permission = permissionName)

                LaunchedEffect(key1 = storagePermissionState.status.isGranted) {
                    if (storagePermissionState.status.isGranted)
                        mediaRepository.onPermissionAccepted()
                }

                CompositionLocalProvider(
                    LocalUserPreferences provides userPreferences,
                    LocalCommonSongsAction provides commonSongsActions
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        AnimatedContent(
                            targetState = storagePermissionState.status is PermissionStatus.Granted,
                            label = ""
                        ) {
                            if (it) {
                                if(usbId == null) {
                                    Text("To play music, insert a flash driver")
                                } else MusicaApp2(modifier = Modifier.fillMaxSize(), navController)
                            } else
                                AskPermissionScreen(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    storagePermissionState.status.shouldShowRationale,
                                    onRequestPermission = { storagePermissionState.launchPermissionRequest() },
                                    onOpenSettings = { openAppSettingsScreen() }
                                )
                        }

                    }
                }
            }
        }
    }

    private fun getReadingMediaPermissionName() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        }
        else Manifest.permission.READ_EXTERNAL_STORAGE

    private fun openAppSettingsScreen() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

}

@Composable
fun SlidingComposable(scroll: Int) {

    Card(
        modifier = Modifier.offset {
            IntOffset(x = 0, y = scroll)
        },
    ) {
        Text(
            text = "Hello I slide out"
        )
    }
}

@Composable
fun ConcertPerformers(
    scrollState: ScrollState,
    venueName: String,
    performers: List<String>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.background(color = Color.LightGray),
            text = "The following performers are performing at $venueName tonight:"
        )

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            for (item in performers) {
                PerformerItem(performer = item)
            }
        }
    }
}

@Composable
fun PerformerItem(performer: String) {
    Card(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .background(
                color = Color.LightGray,
            )
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.padding(10.dp),
            text = performer
        )
    }
}

@Composable
fun BadComposable() {
    var count by remember { mutableStateOf(0) }

//    count++ // Backwards write, writing to state after it has been read</b>
    // Causes recomposition on click
    Button(onClick = { count++ }, Modifier.wrapContentSize()) {
        Text("Recompose")
    }

    Text("$count")
}