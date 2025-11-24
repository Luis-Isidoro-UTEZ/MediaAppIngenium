// --- 8. MediaViewModelFactory --- Necesario para pasar el Repositorio al ViewModel
package mx.edu.utez.mediaappingenium.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mx.edu.utez.mediaappingenium.data.repository.AppDatabase
import mx.edu.utez.mediaappingenium.data.repository.MediaRepository

class MediaViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MediaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val database = AppDatabase.getDatabase(application)
            val repository = MediaRepository(database.mediaDao())
            return MediaViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}