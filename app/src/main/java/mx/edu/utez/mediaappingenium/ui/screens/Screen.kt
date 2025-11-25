// --- 11. Definición de Rutas de Navegación ---
package mx.edu.utez.mediaappingenium.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.Text

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Recording : Screen("recording", "Grabar", Icons.Default.Mic)
    object AudioList : Screen("audio", "Audios", Icons.Default.AudioFile)
    object ImageList : Screen("images", "Imágenes", Icons.Default.Image)
    object VideoList : Screen("videos", "Videos", Icons.Default.Videocam)
    // Pantalla de detalle (no va en la barra de navegación)
    object VideoPlayer : Screen("video_player/{uri}", "Video Player", Icons.Default.Videocam) {
        fun createRoute(uri: String) = "video_player/$uri"
    }
}
val navBarItems = listOf(
    Screen.Recording,
    Screen.AudioList,
    Screen.ImageList,
    Screen.VideoList,
)

@Preview(showBackground = true)
@Composable
fun BottomNavPreview() {
    var selectedItem by remember { mutableStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navBarItems.forEachIndexed { index, screen ->
                    NavigationBarItem(
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) }
                    )
                }
            }
        }
    ) { padding ->
        // Contenido de ejemplo
        Text(
            text = "Pantalla: ${navBarItems[selectedItem].label}",
            modifier = Modifier.padding(padding)
        )
    }
}