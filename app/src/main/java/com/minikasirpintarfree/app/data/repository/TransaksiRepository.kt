package com.minikasirpintarfree.app.data.repository

import com.minikasirpintarfree.app.data.dao.TransaksiDao
import com.minikasirpintarfree.app.data.model.BestSellingProduct
import com.minikasirpintarfree.app.data.model.Transaksi
import com.minikasirpintarfree.app.data.model.TransaksiItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

class TransaksiRepository(private val transaksiDao: TransaksiDao) {
    private val gson = Gson()
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
    
    fun getRecentTransaksi(limit: Int = 5): Flow<List<Transaksi>> = transaksiDao.getRecentTransaksi(limit)
    
    fun getBestSellingProducts(limit: Int = 5): Flow<List<BestSellingProduct>> {
        return getAllTransaksi().map { transaksiList ->
            val productSalesMap = mutableMapOf<Long, Pair<String, MutableList<TransaksiItem>>>()
            
            // Parse semua transaksi dan kelompokkan berdasarkan produk
            transaksiList.forEach { transaksi ->
                try {
                    val itemsType = object : TypeToken<List<TransaksiItem>>() {}.type
                    val items: List<TransaksiItem> = gson.fromJson(transaksi.items, itemsType)
                    
                    items.forEach { item ->
                        if (productSalesMap.containsKey(item.produkId)) {
                            productSalesMap[item.produkId]!!.second.add(item)
                        } else {
                            productSalesMap[item.produkId] = Pair(item.namaProduk, mutableListOf(item))
                        }
                    }
                } catch (e: Exception) {
                    // Skip transaksi yang gagal di-parse
                }
            }
            
            // Hitung total terjual dan pendapatan untuk setiap produk
            productSalesMap.map { (produkId, data) ->
                val namaProduk = data.first
                val items = data.second
                val totalTerjual = items.sumOf { it.quantity }
                val totalPendapatan = items.sumOf { it.subtotal }
                
                BestSellingProduct(
                    produkId = produkId,
                    namaProduk = namaProduk,
                    totalTerjual = totalTerjual,
                    totalPendapatan = totalPendapatan
                )
            }
            .sortedByDescending { it.totalTerjual }
            .take(limit)
        }
    }
}

