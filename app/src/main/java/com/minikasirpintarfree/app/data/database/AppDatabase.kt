package com.minikasirpintarfree.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
        
        /**
         * ✅ FASE 2.2: Database Migration Strategy
         * 
         * Migrations akan ditambahkan di sini saat ada perubahan skema database.
         * Contoh: Saat update dari version 2 ke version 3, tambahkan MIGRATION_2_3
         * 
         * Contoh Migration (untuk referensi):
         * val MIGRATION_2_3 = object : Migration(2, 3) {
         *     override fun migrate(database: SupportSQLiteDatabase) {
         *         // Contoh: Tambah kolom baru
         *         database.execSQL("ALTER TABLE produk ADD COLUMN supplier TEXT")
         *     }
         * }
         */
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "minikasir_database"
                )
                    // ✅ FASE 2.2: HAPUS fallbackToDestructiveMigration()
                    // fallbackToDestructiveMigration() telah dihapus untuk keamanan data
                    // Jika ada perubahan skema, tambahkan migration dengan .addMigrations()
                    // Contoh: .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

