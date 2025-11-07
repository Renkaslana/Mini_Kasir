package com.minikasirpintarfree.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minikasirpintarfree.app.data.model.Notifikasi
import com.minikasirpintarfree.app.data.repository.NotifikasiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationsViewModel(
    private val notifikasiRepository: NotifikasiRepository
) : ViewModel() {
    
    private val _notifikasiList = MutableStateFlow<List<Notifikasi>>(emptyList())
    val notifikasiList: StateFlow<List<Notifikasi>> = _notifikasiList.asStateFlow()
    
    init {
        loadNotifikasi()
    }
    
    private fun loadNotifikasi() {
        viewModelScope.launch {
            notifikasiRepository.getAllNotifikasi().collect { list ->
                _notifikasiList.value = list
            }
        }
    }
    
    fun markAsRead(id: Long) {
        viewModelScope.launch {
            notifikasiRepository.markAsRead(id)
        }
    }
    
    fun markAllAsRead() {
        viewModelScope.launch {
            notifikasiRepository.markAllAsRead()
        }
    }
    
    fun clearAll() {
        viewModelScope.launch {
            notifikasiRepository.deleteAllNotifikasi()
        }
    }
}

