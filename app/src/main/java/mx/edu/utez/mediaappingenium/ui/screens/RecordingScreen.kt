// --- 15. Screen 1: Grabación ---
package mx.edu.utez.mediaappingenium.ui.screens

import android.Manifest
import android.R.attr.onClick
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import mx.edu.utez.mediaappingenium.data.model.MediaType
import mx.edu.utez.mediaappingenium.data.repository.AudioRecorder
import mx.edu.utez.mediaappingenium.viewmodel.MediaViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun RecordingScreen(
    mediaViewModel: MediaViewModel,
    audioRecorder: AudioRecorder // Recibimos el helper
) {
    val context = LocalContext.current
    var hasPermissions by remember { mutableStateOf(false) }
    // --- Estado de la UI ---
    var isRecordingAudio by remember { mutableStateOf(false) }
    var currentAudioFile by remember { mutableStateOf<File?>(null) }
    // --- Estado para las URIs de Cámara ---
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempVideoUri by remember { mutableStateOf<Uri?>(null) }
    // --- Permisos ---
    val requiredPermissions = getRequiredPermissions()
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasPermissions = permissions.values.all { it }
    }
    LaunchedEffect(Unit) {
        hasPermissions = requiredPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        if (!hasPermissions) {
            permissionLauncher.launch(requiredPermissions)
        }
    }
    // --- ActivityResult Launchers para CAPTURA ---
    // 1. Launcher para Imagen (TakePicture)
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempImageUri?.let { uri ->
                mediaViewModel.insertMediaFromUri(uri, MediaType.IMAGE)
            }
        }
        tempImageUri = null // Limpiar
    }
    // 2. Launcher para Video (CaptureVideo)

    val videoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo()
    ) { success ->
        if (success) {
            tempVideoUri?.let { uri ->
                mediaViewModel.insertMediaFromUri(uri, MediaType.VIDEO)
            }
        }
        tempVideoUri = null // Limpiar
    }
    // --- Lógica de la UI ---
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF021024))
            .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
// ... (dentro de la Column)

        Text(
            text = "Centro de Grabación",
            // 1. Aumentamos el tamaño de la fuente
            fontSize = 32.sp, // headlineMedium es 28.sp, lo subimos a 32.sp
            // 2. Hacemos la fuente más gruesa
            fontWeight = FontWeight.Bold, // Puedes usar .Bold o .SemiBold
            // 3. Aseguramos el color para que sea legible
            color = Color.White,
            // El padding inferior se mantiene
            modifier = Modifier.padding(bottom = 32.dp)
        )

// ...

                    if (!hasPermissions) {
                        Text("Se necesitan permisos de cámara, micrófono y almacenamiento para continuar.")
                        Button(onClick = { permissionLauncher.launch(requiredPermissions) }) {
                            Text("Otorgar Permisos")
                        }
                    } else {
                        // Botón de Grabación de Audio (con estado)
                        RecordingButton(
                            text = if (isRecordingAudio) "Detener Grabación Audio" else "Iniciar Grabación Audio",
                            icon = if (isRecordingAudio) Icons.Default.Stop else Icons.Default.MicNone,
                            buttonColor = if (isRecordingAudio) Color(0XFFf93f03) else Color(
                                0XFF052659
                            ),
                            onClick = {
                                if (isRecordingAudio) {
                                    // --- DETENER ---
                                    audioRecorder.stop()
                                    currentAudioFile?.let {
                                        mediaViewModel.insertMediaFromFile(it, MediaType.AUDIO)
                                    }
                                    isRecordingAudio = false
                                    currentAudioFile = null
                                } else {
                                    // --- INICIAR ---
                                    val audioFile = createTempFile(context, MediaType.AUDIO)
                                    currentAudioFile = audioFile
                                    audioRecorder.start(audioFile)
                                    isRecordingAudio = true
                                }
                            }
                        )
                        Spacer(Modifier.height(30.dp))
                        // Botón de Captura de Imagen
                        RecordingButton(
                            text = "Capturar Imagen",
                            icon = Icons.Default.Image,
                            buttonColor = Color(0XFF052659),
                            // Deshabilitar si ya estamos grabando audio
                            isEnabled = !isRecordingAudio,
                            onClick = {
                                val uri = createTempUri(context, MediaType.IMAGE)
                                tempImageUri = uri
                                imageLauncher.launch(uri)
                            }
                        )
                        Spacer(Modifier.height(30.dp))
                        // Botón de Captura de Video
                        RecordingButton(
                            text = "Capturar Video",
                            icon = Icons.Default.Videocam,
                            buttonColor = Color(0XFF052659),

                            // Deshabilitar si ya estamos grabando audio
                            isEnabled = !isRecordingAudio,
                            onClick = {
                                val uri = createTempUri(context, MediaType.VIDEO)
                                tempVideoUri = uri
                                videoLauncher.launch(uri)
                            }
                        )
                    }
                }
}
// --- Helpers para la RecordingScreen ---
@Composable
private fun RecordingButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isEnabled: Boolean = true,
    buttonColor: Color = MaterialTheme.colorScheme.primary
) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp),
        shape = RoundedCornerShape(25.dp),
        // Elimina el padding de contenido por defecto para tener control total
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        // Usamos un Box para alinear los elementos de forma independiente
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 1. El Icono, alineado al inicio (izquierda)
            Icon(
                imageVector = icon,
                contentDescription = text, // El contentDescription sigue siendo útil para accesibilidad
                modifier = Modifier.align(Alignment.CenterStart) .size(32.dp), // Alineación a la izquierda
                tint = Color.White
            )
            // 2. El Texto, alineado al centro del Box
            Text(
                text = text,
                modifier = Modifier.align(Alignment.Center), // Alineación en el centro del botón
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

private fun getRequiredPermissions(): Array<String> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
        // Ya no pedimos permisos de LECTURA, ya que creamos nuestro propio contenido
        // (Si aún quisieras una galería, los necesitarías)
        )
    } else {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            // Necesario para < 29 para MediaRecorder
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
}
/**
 * Crea un archivo temporal en el almacenamiento externo de la app.
 */
private fun createTempFile(context: Context, mediaType: MediaType): File {
    val (dir, extension) = when (mediaType) {
        MediaType.AUDIO -> Pair(Environment.DIRECTORY_MUSIC, ".mp3")
        MediaType.IMAGE -> Pair(Environment.DIRECTORY_PICTURES, ".jpg")
        MediaType.VIDEO -> Pair(Environment.DIRECTORY_MOVIES, ".mp4")
    }
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val fileName = "${mediaType.name}_${timestamp}$extension"
    val storageDir = context.getExternalFilesDir(dir)
    return File(storageDir, fileName)
}
/**
 * Crea una content:// Uri segura para un archivo temporal usando FileProvider.
 * Esto es lo que necesitan TakePicture y CaptureVideo.

 */
private fun createTempUri(context: Context, mediaType: MediaType): Uri {
    val file = createTempFile(context, mediaType)
    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context, authority, file)
}