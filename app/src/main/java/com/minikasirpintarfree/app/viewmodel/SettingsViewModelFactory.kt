package com.minikasirpintarfree.app.viewmodel

import androidx.lifecycle.ViewModelProvider
import com.minikasirpintarfree.app.data.repository.ProdukRepository
import com.minikasirpintarfree.app.data.repository.TransaksiRepository

class SettingsViewModelFactory(
    private val produkRepository: ProdukRepository,
    private val transaksiRepository: TransaksiRepository,
    private val sharedPreferences: android.content.SharedPreferences
) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(produkRepository, transaksiRepository, sharedPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

