// --- 19. Screen 5: Reproductor de Video ---
package mx.edu.utez.mediaappingenium.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.ui.PlayerView
import mx.edu.utez.mediaappingenium.viewmodel.PlaybackViewModel

@Composable
fun VideoPlayerScreen(
    uri: Uri,
    playbackViewModel: PlaybackViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    // Inicia la reproducción cuando el Composable entra en la pantalla
    LaunchedEffect(uri) {
        playbackViewModel.playMedia(uri.toString())
    }

    // Manejo del ciclo de vida para liberar el reproductor
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> playbackViewModel.exoPlayer.pause()
                Lifecycle.Event.ON_RESUME -> playbackViewModel.exoPlayer.play()
                Lifecycle.Event.ON_DESTROY -> playbackViewModel.releasePlayer()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            // No liberamos aquí, porque el VM persiste.
            // La liberación se maneja en el onCleared del VM
            // o al navegar hacia atrás (que es más complejo).
            // Por ahora, pausamos.
            playbackViewModel.exoPlayer.pause()
        }
    }
    // Usamos AndroidView para mostrar el PlayerView de ExoPlayer
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    player = playbackViewModel.exoPlayer
                    useController = true
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}