// --- Screen 3: Lista de Imágenes (Adaptada al estilo de AudioList) ---
package mx.edu.utez.mediaappingenium.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import mx.edu.utez.mediaappingenium.data.model.MediaItem
import mx.edu.utez.mediaappingenium.utils.formatDate
import mx.edu.utez.mediaappingenium.viewmodel.MediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageListScreen(mediaViewModel: MediaViewModel) {
    val imageList by mediaViewModel.allImages.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis Imágenes", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF052659)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1C1C1C))
                .padding(padding)
        ) {
            if (imageList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No hay imágenes capturadas.",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // 1. Reemplazamos HorizontalPager y Box por LazyColumn
                // Es el componente ideal para listas verticales eficientes.
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp), // Espaciado general
                    verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre cada tarjeta
                ) {
                    items(imageList) { item ->
                        // Usamos un nuevo Composable 'ImageCard' para cada elemento,
                        // similar a como se usa 'AudioCard'.
                        ImageCard(item = item)
                    }
                }
            }
        }
    }
}

/**
 * Composable que define el estilo de cada fila en la lista de imágenes,
 * imitando el diseño de AudioCard.
 */
@Composable
fun ImageCard(item: MediaItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // Usamos el mismo color de fondo que AudioCard
                .background(Color(0xff4a5c6a))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 2. La miniatura de la imagen, alineada a la izquierda.
            // La hacemos pequeña y cuadrada.
            AsyncImage(
                model = item.uri.toUri(),
                contentDescription = "Miniatura de ${item.name}",
                // Le damos el tamaño y forma deseados.
                modifier = Modifier
                    .size(56.dp) // Tamaño similar al de un icono grande.
                    .clip(RoundedCornerShape(8.dp)), // Bordes redondeados.
                contentScale = ContentScale.Crop // 'Crop' asegura que la imagen llene el espacio sin deformarse.
            )

            Spacer(Modifier.width(16.dp))

            // 3. La columna con el nombre y la fecha.
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    color = Color(0xFFccd0cf),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1, // Evita que el nombre ocupe varias líneas
                    overflow = TextOverflow.Ellipsis // Añade "..." si el nombre es muy largo
                )
                Text(
                    text = formatDate(item.date),
                    color = Color(0xFFccd0cf),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
