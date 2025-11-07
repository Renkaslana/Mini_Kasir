package com.minikasirpintarfree.app.data.repository

import com.minikasirpintarfree.app.data.dao.TransaksiDao
import com.minikasirpintarfree.app.data.model.Transaksi
import kotlinx.coroutines.flow.Flow
import java.util.Date

class TransaksiRepository(private val transaksiDao: TransaksiDao) {
    fun getAllTransaksi(): Flow<List<Transaksi>> = transaksiDao.getAllTransaksi()
    
    suspend fun getTransaksiById(id: Long): Transaksi? = transaksiDao.getTransaksiById(id)
    
    fun getTransaksiHariIni(): Flow<List<Transaksi>> = transaksiDao.getTransaksiHariIni()
    
    fun getTransaksiMingguIni(): Flow<List<Transaksi>> = transaksiDao.getTransaksiMingguIni()
    
    fun getTransaksiBulanIni(): Flow<List<Transaksi>> = transaksiDao.getTransaksiBulanIni()
    
    fun getTransaksiByDateRange(startDate: Date, endDate: Date): Flow<List<Transaksi>> = 
        transaksiDao.getTransaksiByDateRange(startDate, endDate)
    
    suspend fun insertTransaksi(transaksi: Transaksi): Long = transaksiDao.insertTransaksi(transaksi)
    
    suspend fun deleteAllTransaksi() = transaksiDao.deleteAllTransaksi()
    
    fun getTotalTransaksiHariIni(): Flow<Int> = transaksiDao.getTotalTransaksiHariIni()
    
    fun getTotalPendapatanHariIni(): Flow<Double?> = transaksiDao.getTotalPendapatanHariIni()
}

