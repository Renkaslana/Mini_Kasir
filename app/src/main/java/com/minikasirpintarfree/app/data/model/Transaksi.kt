package com.minikasirpintarfree.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transaksi")
data class Transaksi(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tanggal: Date,
    val totalHarga: Double,
    val uangDiterima: Double,
    val kembalian: Double,
    val items: String // JSON string of list of TransaksiItem
)

data class TransaksiItem(
    val produkId: Long,
    val namaProduk: String,
    val harga: Double,
    val quantity: Int,
    val subtotal: Double
)

