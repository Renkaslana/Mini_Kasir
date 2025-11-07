package com.minikasirpintarfree.app.data.dao

import androidx.room.*
import com.minikasirpintarfree.app.data.model.Transaksi
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TransaksiDao {
    @Query("SELECT * FROM transaksi ORDER BY tanggal DESC")
    fun getAllTransaksi(): Flow<List<Transaksi>>
    
    @Query("SELECT * FROM transaksi WHERE id = :id")
    suspend fun getTransaksiById(id: Long): Transaksi?
    
    @Query("SELECT * FROM transaksi WHERE date(tanggal/1000, 'unixepoch') = date('now')")
    fun getTransaksiHariIni(): Flow<List<Transaksi>>
    
    @Query("SELECT * FROM transaksi WHERE date(tanggal/1000, 'unixepoch') >= date('now', '-7 days')")
    fun getTransaksiMingguIni(): Flow<List<Transaksi>>
    
    @Query("SELECT * FROM transaksi WHERE date(tanggal/1000, 'unixepoch') >= date('now', 'start of month')")
    fun getTransaksiBulanIni(): Flow<List<Transaksi>>
    
    @Query("SELECT * FROM transaksi WHERE tanggal BETWEEN :startDate AND :endDate ORDER BY tanggal DESC")
    fun getTransaksiByDateRange(startDate: Date, endDate: Date): Flow<List<Transaksi>>
    
    @Insert
    suspend fun insertTransaksi(transaksi: Transaksi): Long
    
    @Query("DELETE FROM transaksi")
    suspend fun deleteAllTransaksi()
    
    @Query("SELECT COUNT(*) FROM transaksi WHERE date(tanggal/1000, 'unixepoch') = date('now')")
    fun getTotalTransaksiHariIni(): Flow<Int>
    
    @Query("SELECT SUM(totalHarga) FROM transaksi WHERE date(tanggal/1000, 'unixepoch') = date('now')")
    fun getTotalPendapatanHariIni(): Flow<Double?>
}

