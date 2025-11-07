package com.minikasirpintarfree.app.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.minikasirpintarfree.app.R
import com.minikasirpintarfree.app.data.database.AppDatabase
import com.minikasirpintarfree.app.data.repository.ProdukRepository
import com.minikasirpintarfree.app.data.repository.TransaksiRepository
import com.minikasirpintarfree.app.databinding.ActivityDashboardBinding
import com.minikasirpintarfree.app.ui.login.LoginActivity
import com.minikasirpintarfree.app.ui.produk.ProdukActivity
import com.minikasirpintarfree.app.ui.transaksi.TransaksiActivity
import com.minikasirpintarfree.app.ui.laporan.LaporanActivity
import com.minikasirpintarfree.app.ui.notifications.NotificationsActivity
import com.minikasirpintarfree.app.ui.settings.SettingsActivity
import com.minikasirpintarfree.app.utils.NotificationHelper
import com.minikasirpintarfree.app.viewmodel.DashboardViewModel
import com.minikasirpintarfree.app.viewmodel.DashboardViewModelFactory

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var viewModel: DashboardViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityDashboardBinding.inflate(layoutInflater)
            setContentView(binding.root)
            
            val database = AppDatabase.getDatabase(this)
            val produkRepository = ProdukRepository(database.produkDao())
            val transaksiRepository = TransaksiRepository(database.transaksiDao())
            
            viewModel = ViewModelProvider(this, DashboardViewModelFactory(produkRepository, transaksiRepository))[DashboardViewModel::class.java]
            
            setSupportActionBar(binding.toolbar)
            
            NotificationHelper.createNotificationChannel(this)
            
            setupBottomNavigation()
            setupClickListeners()
            observeViewModel()
        } catch (e: Exception) {
            android.util.Log.e("DashboardActivity", "Error in onCreate", e)
            Toast.makeText(this, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
    
    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Already on dashboard
                    true
                }
                R.id.nav_produk -> {
                    startActivity(Intent(this, ProdukActivity::class.java))
                    true
                }
                R.id.nav_transaksi -> {
                    startActivity(Intent(this, TransaksiActivity::class.java))
                    true
                }
                R.id.nav_laporan -> {
                    startActivity(Intent(this, LaporanActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.cardProduk.setOnClickListener {
            startActivity(Intent(this, ProdukActivity::class.java))
        }
        
        binding.cardTransaksi.setOnClickListener {
            startActivity(Intent(this, TransaksiActivity::class.java))
        }
        
        binding.cardLaporan.setOnClickListener {
            startActivity(Intent(this, LaporanActivity::class.java))
        }
        
        binding.cardSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
    
    private fun observeViewModel() {
        viewModel.totalProduk.observe(this) { total: Int ->
            binding.tvTotalProduk.text = total.toString()
        }
        
        viewModel.totalTransaksiHariIni.observe(this) { total: Int ->
            binding.tvTotalTransaksi.text = total.toString()
        }
        
        viewModel.stokMenipis.observe(this) { total: Int ->
            binding.tvStokMenipis.text = total.toString()
            // Also check for notifications
            if (total > 0) {
                NotificationHelper.showNotification(
                    this,
                    "Peringatan Stok",
                    "Ada $total produk dengan stok menipis",
                    NotificationHelper.NOTIFICATION_ID_LOW_STOCK
                )
            }
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_dashboard, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_notifications -> {
                startActivity(Intent(this, NotificationsActivity::class.java))
                true
            }
            R.id.menu_logout -> {
                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                sharedPreferences.edit().putBoolean("is_logged_in", false).apply()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
}

