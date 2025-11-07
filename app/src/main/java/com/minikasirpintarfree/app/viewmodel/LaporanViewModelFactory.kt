package com.minikasirpintarfree.app.viewmodel

import androidx.lifecycle.ViewModelProvider
import com.minikasirpintarfree.app.data.repository.TransaksiRepository

class LaporanViewModelFactory(private val transaksiRepository: TransaksiRepository) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LaporanViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LaporanViewModel(transaksiRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

