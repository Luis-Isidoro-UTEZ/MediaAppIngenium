// --- 4. Repositorio (Control de datos guardados con Room) —
package mx.edu.utez.mediaappingenium.data.repository

import kotlinx.coroutines.flow.Flow
import mx.edu.utez.mediaappingenium.data.model.MediaDao
import mx.edu.utez.mediaappingenium.data.model.MediaItem
import mx.edu.utez.mediaappingenium.data.model.MediaType

class MediaRepository(private val mediaDao: MediaDao) {

    fun getAllAudio(): Flow<List<MediaItem>> {
        return mediaDao.getMediaByType(MediaType.AUDIO)
    }

    fun getAllImages(): Flow<List<MediaItem>> {
        return mediaDao.getMediaByType(MediaType.IMAGE)
    }

    fun getAllVideos(): Flow<List<MediaItem>> {
        return mediaDao.getMediaByType(MediaType.VIDEO)
    }

    suspend fun insertMedia(item: MediaItem) {
        mediaDao.insertMedia(item)
    }
}