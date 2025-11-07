package com.minikasirpintarfree.app.viewmodel

import androidx.lifecycle.ViewModelProvider
import com.minikasirpintarfree.app.data.repository.TransaksiRepository
import com.minikasirpintarfree.app.data.repository.ProdukRepository

class TransaksiViewModelFactory(
    private val transaksiRepository: TransaksiRepository,
    private val produkRepository: ProdukRepository
) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransaksiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransaksiViewModel(transaksiRepository, produkRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

