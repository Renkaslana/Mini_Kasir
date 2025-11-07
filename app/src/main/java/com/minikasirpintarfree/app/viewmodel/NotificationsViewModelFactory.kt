package com.minikasirpintarfree.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.minikasirpintarfree.app.data.repository.NotifikasiRepository

class NotificationsViewModelFactory(
    private val notifikasiRepository: NotifikasiRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificationsViewModel(notifikasiRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

