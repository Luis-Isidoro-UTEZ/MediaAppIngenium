// --- 12. Navegación ---
package mx.edu.utez.mediaappingenium.ui

import android.app.Application
import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import mx.edu.utez.mediaappingenium.data.repository.AudioRecorder
import mx.edu.utez.mediaappingenium.ui.screens.AudioListScreen
import mx.edu.utez.mediaappingenium.ui.screens.ImageListScreen
import mx.edu.utez.mediaappingenium.ui.screens.RecordingScreen
import mx.edu.utez.mediaappingenium.ui.screens.Screen
import mx.edu.utez.mediaappingenium.ui.screens.VideoListScreen
import mx.edu.utez.mediaappingenium.ui.screens.VideoPlayerScreen
import mx.edu.utez.mediaappingenium.viewmodel.MediaViewModel
import mx.edu.utez.mediaappingenium.viewmodel.MediaViewModelFactory
import mx.edu.utez.mediaappingenium.viewmodel.PlaybackViewModel
import mx.edu.utez.mediaappingenium.viewmodel.PlaybackViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val application = context.applicationContext as Application
    // Factories para los ViewModels
    val mediaViewModel: MediaViewModel = viewModel(
        factory = MediaViewModelFactory(application)
    )
    val playbackViewModel: PlaybackViewModel = viewModel(
        factory = PlaybackViewModelFactory(application)
    )
    // AudioRecorder para la grabación de audio
    val audioRecorder = remember { AudioRecorder(context) }
    Scaffold(
        bottomBar = {
            AppBottomNavBar(navController = navController)
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Recording.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Recording.route) {
                RecordingScreen(
                    mediaViewModel = mediaViewModel,
                    audioRecorder = audioRecorder
                )
            }
            composable(Screen.AudioList.route) {
                AudioListScreen(
                    mediaViewModel = mediaViewModel,
                    playbackViewModel = playbackViewModel
                )
            }
            composable(Screen.ImageList.route) {
                ImageListScreen(mediaViewModel = mediaViewModel)
            }

            composable(Screen.VideoList.route) {
                VideoListScreen(
                    mediaViewModel = mediaViewModel,
                    navController = navController
                )
            }
            composable(
                route = Screen.VideoPlayer.route,
                arguments = listOf(navArgument("uri") { type = NavType.StringType })
            ) { backStackEntry ->
                val uri = backStackEntry.arguments?.getString("uri")
                if (uri != null) {
                    VideoPlayerScreen(
                        uri = Uri.parse(Uri.decode(uri)),
                        playbackViewModel = playbackViewModel
                    )
                }
            }
        }
    }
}