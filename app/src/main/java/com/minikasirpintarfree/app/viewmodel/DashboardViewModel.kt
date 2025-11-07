package com.minikasirpintarfree.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minikasirpintarfree.app.data.repository.ProdukRepository
import com.minikasirpintarfree.app.data.repository.TransaksiRepository
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val produkRepository: ProdukRepository,
    private val transaksiRepository: TransaksiRepository
) : ViewModel() {
    
    private val _totalProduk = MutableLiveData<Int>()
    val totalProduk: LiveData<Int> = _totalProduk
    
    private val _totalTransaksiHariIni = MutableLiveData<Int>()
    val totalTransaksiHariIni: LiveData<Int> = _totalTransaksiHariIni
    
    private val _stokMenipis = MutableLiveData<Int>()
    val stokMenipis: LiveData<Int> = _stokMenipis
    
    init {
        loadDashboardData()
    }
    
    private fun loadDashboardData() {
        viewModelScope.launch {
            try {
                produkRepository.getTotalProduk().collect { total ->
                    _totalProduk.postValue(total)
                }
            } catch (e: Exception) {
                _totalProduk.postValue(0)
            }
        }
        
        viewModelScope.launch {
            try {
                transaksiRepository.getTotalTransaksiHariIni().collect { total ->
                    _totalTransaksiHariIni.postValue(total)
                }
            } catch (e: Exception) {
                _totalTransaksiHariIni.postValue(0)
            }
        }
        
        viewModelScope.launch {
            try {
                produkRepository.getProdukStokMenipis(10).collect { list ->
                    _stokMenipis.postValue(list.size)
                }
            } catch (e: Exception) {
                _stokMenipis.postValue(0)
            }
        }
    }
}

