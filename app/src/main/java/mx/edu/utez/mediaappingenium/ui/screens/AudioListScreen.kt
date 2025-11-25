// --- 16. Screen 2: Lista de Audio ---
package mx.edu.utez.mediaappingenium.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.SensorsOff
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mx.edu.utez.mediaappingenium.data.model.MediaItem
import mx.edu.utez.mediaappingenium.utils.formatDate
import mx.edu.utez.mediaappingenium.utils.formatDuration
import mx.edu.utez.mediaappingenium.viewmodel.MediaViewModel
import mx.edu.utez.mediaappingenium.viewmodel.PlaybackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioListScreen(
    mediaViewModel: MediaViewModel,
    playbackViewModel: PlaybackViewModel
) {
    val audioList by mediaViewModel.allAudio.collectAsState()
    val isPlaying by playbackViewModel.isPlaying.collectAsState()
    val isAccelerometerOn by playbackViewModel.isAccelerometerEnabled.collectAsState()
    val currentVolume by playbackViewModel.currentVolume.collectAsState()
    var currentlyPlayingUri by remember { mutableStateOf<String?>(null) }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis Audios", color = Color.White, fontWeight = FontWeight.Bold) },
                actions = {
                    Text(
                        text = "Vol: ${(currentVolume * 100).toInt()}%",
                        modifier = Modifier.padding(end = 8.dp),
                        color = Color.White
                    )
                    IconButton(onClick = { playbackViewModel.toggleAccelerometer() }) {
                        Icon(
                            if (isAccelerometerOn) Icons.Default.Sensors else Icons.Default.SensorsOff,
                            contentDescription = "Control por Acelerómetro",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF052659)
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xff06141B))
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (audioList.isEmpty()) {
                item {
                    Text(
                        "No hay audios grabados.",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
            items(audioList) { item ->
                AudioCard(
                    item = item,
                    isPlaying = isPlaying && currentlyPlayingUri == item.uri,
                    onPlayClick = {
                        if (currentlyPlayingUri == item.uri) {
                            playbackViewModel.togglePlayPause()
                        } else {
                            playbackViewModel.playMedia(item.uri)
                            currentlyPlayingUri = item.uri
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AudioCard(
    item: MediaItem,
    isPlaying: Boolean,
    onPlayClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),

        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xff4a5c6a))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Audiotrack,
                contentDescription = "Audio",
                tint = Color(0xFFccd0cf),
                modifier = Modifier.size(40.dp)
            )

            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    color = (Color(0xFFccd0cf)),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatDuration(item.duration),
                    color = (Color(0xFFccd0cf)),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = formatDate(item.date),
                    color = (Color(0xFFccd0cf)),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Spacer(Modifier.width(16.dp))
            IconButton(
                onClick = onPlayClick,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    if (isPlaying) Icons.Default.Stop else Icons.Default.SmartDisplay,
                    contentDescription = "Play/Pause",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}