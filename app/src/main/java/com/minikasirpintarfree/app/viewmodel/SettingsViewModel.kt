package com.minikasirpintarfree.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minikasirpintarfree.app.data.repository.ProdukRepository
import com.minikasirpintarfree.app.data.repository.TransaksiRepository
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val produkRepository: ProdukRepository,
    private val transaksiRepository: TransaksiRepository,
    private val sharedPreferences: android.content.SharedPreferences
) : ViewModel() {
    
    fun resetDataProduk(onComplete: () -> Unit) {
        viewModelScope.launch {
            produkRepository.deleteAllProduk()
            onComplete()
        }
    }
    
    fun resetDataTransaksi(onComplete: () -> Unit) {
        viewModelScope.launch {
            transaksiRepository.deleteAllTransaksi()
            onComplete()
        }
    }
    
    fun resetAllData(onComplete: () -> Unit) {
        viewModelScope.launch {
            produkRepository.deleteAllProduk()
            transaksiRepository.deleteAllTransaksi()
            onComplete()
        }
    }
    
    fun getTheme(): String {
        return sharedPreferences.getString("theme", "light") ?: "light"
    }
    
    fun setTheme(theme: String) {
        viewModelScope.launch {
            sharedPreferences.edit().putString("theme", theme).apply()
        }
    }
}

