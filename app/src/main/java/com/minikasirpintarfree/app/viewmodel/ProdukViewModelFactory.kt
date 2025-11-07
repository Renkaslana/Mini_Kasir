package com.minikasirpintarfree.app.viewmodel

import androidx.lifecycle.ViewModelProvider
import com.minikasirpintarfree.app.data.repository.ProdukRepository

class ProdukViewModelFactory(private val produkRepository: ProdukRepository) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProdukViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProdukViewModel(produkRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

