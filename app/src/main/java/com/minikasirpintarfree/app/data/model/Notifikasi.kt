package com.minikasirpintarfree.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "notifikasi")
data class Notifikasi(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val message: String,
    val type: String, // "TRANSACTION", "LOW_STOCK", "WEEKLY_REPORT", dll
    val timestamp: Date = Date(),
    val isRead: Boolean = false
)

