package com.minikasirpintarfree.app.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.minikasirpintarfree.app.MainActivity
import com.minikasirpintarfree.app.R
import com.minikasirpintarfree.app.data.database.AppDatabase
import com.minikasirpintarfree.app.data.model.Notifikasi
import com.minikasirpintarfree.app.data.repository.NotifikasiRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object NotificationHelper {
    private const val CHANNEL_ID = "minikasir_notifications"
    private const val CHANNEL_NAME = "Mini Kasir Notifications"
    
    const val NOTIFICATION_ID_TRANSACTION = 1001
    const val NOTIFICATION_ID_LOW_STOCK = 1002
    const val NOTIFICATION_ID_WEEKLY_REPORT = 1003
    
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Notifikasi untuk transaksi, stok, dan laporan"
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun saveNotificationToDatabase(context: Context, title: String, message: String, type: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getDatabase(context)
                val repository = NotifikasiRepository(database.notifikasiDao())
                val notifikasi = Notifikasi(
                    title = title,
                    message = message,
                    type = type
                )
                repository.insertNotifikasi(notifikasi)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun showNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int,
        type: String = "GENERAL"
    ) {
        createNotificationChannel(context)
        
        // Save to database
        saveNotificationToDatabase(context, title, message, type)
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }
    
    fun showTransactionSuccessNotification(context: Context, total: Double) {
        showNotification(
            context,
            "Transaksi Berhasil",
            "Transaksi senilai ${formatCurrency(total)} berhasil diproses",
            NOTIFICATION_ID_TRANSACTION,
            "TRANSACTION"
        )
    }
    
    fun showLowStockNotification(context: Context, productName: String, stock: Int) {
        showNotification(
            context,
            "Stok Menipis",
            "$productName tersisa $stock unit. Segera lakukan restock!",
            NOTIFICATION_ID_LOW_STOCK,
            "LOW_STOCK"
        )
    }
    
    fun showWeeklyReportNotification(context: Context, totalRevenue: Double) {
        showNotification(
            context,
            "Laporan Mingguan",
            "Total pendapatan minggu ini: ${formatCurrency(totalRevenue)}",
            NOTIFICATION_ID_WEEKLY_REPORT,
            "WEEKLY_REPORT"
        )
    }
    
    private fun formatCurrency(amount: Double): String {
        val format = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("id", "ID"))
        return format.format(amount)
    }
}

