package mx.edu.utez.mediaappingenium.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.AsyncImage
import mx.edu.utez.mediaappingenium.data.model.MediaItem
import mx.edu.utez.mediaappingenium.utils.formatDate
import mx.edu.utez.mediaappingenium.utils.formatDuration
import mx.edu.utez.mediaappingenium.viewmodel.MediaViewModel

// --- Definición de tu Paleta de Colores ---
val ColorBackground = Color(0xFF052659)
val ColorCardBg = Color(0xFF11212D)
val ColorAccent = Color(0xFF253745)
val ColorIconMuted = Color(0xFF4A5C6A)
val ColorTextSecondary = Color(0xFF9BA8AB)
val ColorTextPrimary = Color(0xFFCCD0CF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoListScreen(
    mediaViewModel: MediaViewModel,
    navController: NavController
) {
    val videoList by mediaViewModel.allVideos.collectAsState()

    Scaffold(
        containerColor = Color(0xFF1C1C1C),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Mis Videos",
                        color = ColorTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = ColorBackground,
                    scrolledContainerColor = ColorCardBg
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (videoList.isEmpty()) {
                item {
                    EmptyStateView()
                }
            }
            items(videoList) { item ->
                VideoCard(
                    item = item,
                    onClick = {
                        val encodedUri = Uri.encode(item.uri)
                        navController.navigate(Screen.VideoPlayer.createRoute(encodedUri))
                    }
                )
            }
        }
    }
}

@Composable
fun EmptyStateView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.VideocamOff,
            contentDescription = null,
            tint = ColorIconMuted,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No hay videos grabados.",
            style = MaterialTheme.typography.titleMedium,
            color = ColorTextSecondary
        )
    }
}

@Composable
fun VideoCard(item: MediaItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = ColorCardBg
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- Miniatura con Icono de Play ---
            Box(contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = item.uri.toUri(),
                    contentDescription = "Miniatura",
                    modifier = Modifier
                        .size(100.dp, 75.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ColorAccent),
                    contentScale = ContentScale.Crop
                )
                // Overlay semicurvo oscuro para que resalte el icono
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = ColorTextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            // --- Información del Video ---
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ColorTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Fila para Duración
                MetadataRow(
                    icon = Icons.Default.Schedule,
                    text = formatDuration(item.duration)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Fila para Fecha
                MetadataRow(
                    icon = Icons.Default.CalendarToday,
                    text = formatDate(item.date)
                )
            }
        }
    }
}

@Composable
fun MetadataRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ColorIconMuted, // Color iconos apagados
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = ColorTextSecondary // Color texto secundario
        )
    }
}