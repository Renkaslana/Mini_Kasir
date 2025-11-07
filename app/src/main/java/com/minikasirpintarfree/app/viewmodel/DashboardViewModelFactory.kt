package com.minikasirpintarfree.app.viewmodel

import androidx.lifecycle.ViewModelProvider
import com.minikasirpintarfree.app.data.repository.ProdukRepository
import com.minikasirpintarfree.app.data.repository.TransaksiRepository

class DashboardViewModelFactory(
    private val produkRepository: ProdukRepository,
    private val transaksiRepository: TransaksiRepository
) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(produkRepository, transaksiRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

