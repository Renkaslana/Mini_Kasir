package com.minikasirpintarfree.app.data.dao

import androidx.room.*
import com.minikasirpintarfree.app.data.model.Notifikasi
import kotlinx.coroutines.flow.Flow

@Dao
interface NotifikasiDao {
    @Query("SELECT * FROM notifikasi ORDER BY timestamp DESC")
    fun getAllNotifikasi(): Flow<List<Notifikasi>>
    
    @Query("SELECT * FROM notifikasi WHERE isRead = 0 ORDER BY timestamp DESC")
    fun getUnreadNotifikasi(): Flow<List<Notifikasi>>
    
    @Query("SELECT COUNT(*) FROM notifikasi WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifikasi(notifikasi: Notifikasi): Long
    
    @Update
    suspend fun updateNotifikasi(notifikasi: Notifikasi)
    
    @Query("UPDATE notifikasi SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)
    
    @Query("UPDATE notifikasi SET isRead = 1")
    suspend fun markAllAsRead()
    
    @Query("DELETE FROM notifikasi WHERE id = :id")
    suspend fun deleteNotifikasi(id: Long)
    
    @Query("DELETE FROM notifikasi")
    suspend fun deleteAllNotifikasi()
}

