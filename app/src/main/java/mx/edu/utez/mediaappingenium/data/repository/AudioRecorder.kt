// --- 6. AudioRecorder(ayudante para grabar audio) —
package mx.edu.utez.mediaappingenium.data.repository

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File

class AudioRecorder(private val context: Context) {

    private var recorder: MediaRecorder? = null

    private fun createRecorder(): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            // Deprecated en API 31, pero necesario para < 31
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
    }

    fun start(outputFile: File) {
        // Asegúrate de detener cualquier grabación anterior
        stop()

        recorder = createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile.absolutePath)

            try {
                prepare()
                start()
            } catch (e: Exception) {
                e.printStackTrace()
                // Liberar en caso de error de preparación
                recorder?.release()
                recorder = null
            }
        }
    }

    fun stop() {
        try {
            recorder?.stop()
            recorder?.release()
        } catch (e: Exception) {
            // A veces stop() falla si se llama muy rápido
            e.printStackTrace()
            recorder?.release()
        }
        recorder = null
    }
}