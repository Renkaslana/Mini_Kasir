package com.minikasirpintarfree.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.minikasirpintarfree.app.data.dao.NotifikasiDao
import com.minikasirpintarfree.app.data.dao.ProdukDao
import com.minikasirpintarfree.app.data.dao.TransaksiDao
import com.minikasirpintarfree.app.data.model.Notifikasi
import com.minikasirpintarfree.app.data.model.Produk
import com.minikasirpintarfree.app.data.model.Transaksi

@Database(
    entities = [Produk::class, Transaksi::class, Notifikasi::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun produkDao(): ProdukDao
    abstract fun transaksiDao(): TransaksiDao
    abstract fun notifikasiDao(): NotifikasiDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "minikasir_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

