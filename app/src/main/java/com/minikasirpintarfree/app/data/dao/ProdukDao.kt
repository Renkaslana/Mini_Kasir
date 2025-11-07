package com.minikasirpintarfree.app.data.dao

import androidx.room.*
import com.minikasirpintarfree.app.data.model.Produk
import kotlinx.coroutines.flow.Flow

@Dao
interface ProdukDao {
    @Query("SELECT * FROM produk ORDER BY nama ASC")
    fun getAllProduk(): Flow<List<Produk>>
    
    @Query("SELECT * FROM produk WHERE id = :id")
    suspend fun getProdukById(id: Long): Produk?
    
    @Query("SELECT * FROM produk WHERE barcode = :barcode LIMIT 1")
    suspend fun getProdukByBarcode(barcode: String): Produk?
    
    @Query("SELECT * FROM produk WHERE nama LIKE '%' || :query || '%' OR kategori LIKE '%' || :query || '%'")
    fun searchProduk(query: String): Flow<List<Produk>>
    
    @Query("SELECT * FROM produk WHERE stok <= :threshold")
    fun getProdukStokMenipis(threshold: Int = 10): Flow<List<Produk>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduk(produk: Produk): Long
    
    @Update
    suspend fun updateProduk(produk: Produk)
    
    @Delete
    suspend fun deleteProduk(produk: Produk)
    
    @Query("DELETE FROM produk")
    suspend fun deleteAllProduk()
    
    @Query("SELECT COUNT(*) FROM produk")
    fun getTotalProduk(): Flow<Int>
}

