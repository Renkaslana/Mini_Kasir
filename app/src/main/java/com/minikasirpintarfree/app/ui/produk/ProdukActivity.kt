@file:Suppress("DEPRECATION")

package com.minikasirpintarfree.app.ui.produk

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import com.google.zxing.integration.android.IntentIntegrator
import com.minikasirpintarfree.app.R
import com.minikasirpintarfree.app.data.database.AppDatabase
import com.minikasirpintarfree.app.data.model.Produk
import com.minikasirpintarfree.app.data.repository.ProdukRepository
import com.minikasirpintarfree.app.databinding.ActivityProdukBinding
import com.minikasirpintarfree.app.ui.dashboard.DashboardActivity
import com.minikasirpintarfree.app.ui.notifications.NotificationsActivity
import com.minikasirpintarfree.app.utils.NotificationHelper
import com.minikasirpintarfree.app.viewmodel.ProdukViewModel
import com.minikasirpintarfree.app.viewmodel.ProdukViewModelFactory

class ProdukActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProdukBinding
    private lateinit var viewModel: ProdukViewModel
    private lateinit var adapter: ProdukAdapter
    private val CAMERA_PERMISSION_CODE = 100
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityProdukBinding.inflate(layoutInflater)
            setContentView(binding.root)
            
            val database = AppDatabase.getDatabase(this)
            val produkRepository = ProdukRepository(database.produkDao())
            viewModel = ViewModelProvider(this, ProdukViewModelFactory(produkRepository))[ProdukViewModel::class.java]
            
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            
            setupRecyclerView()
            setupClickListeners()
            observeViewModel()
            checkLowStockNotifications()
        } catch (e: Exception) {
            android.util.Log.e("ProdukActivity", "Error in onCreate", e)
            Toast.makeText(this, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
    
    private fun setupRecyclerView() {
        adapter = ProdukAdapter(
            onItemClick = { produk ->
                // Open edit dialog
                showEditProdukDialog(produk)
            },
            onItemDelete = { produk ->
                viewModel.deleteProduk(produk)
            }
        )
        binding.recyclerViewProduk.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewProduk.adapter = adapter
    }
    
    private fun setupClickListeners() {
        binding.fabAddProduk.setOnClickListener {
            showAddProdukDialog()
        }
        
        binding.btnScanBarcode.setOnClickListener {
            if (checkCameraPermission()) {
                startBarcodeScanner()
            } else {
                requestCameraPermission()
            }
        }
        
        binding.etSearch.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchProduk(it) }
                return true
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    viewModel.loadAllProduk()
                } else {
                    viewModel.searchProduk(newText)
                }
                return true
            }
        })
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.produkList.collect { list: List<Produk> ->
                adapter.submitList(list)
            }
        }
        
        viewModel.errorMessage.observe(this) { message: String ->
            Toast.makeText(this@ProdukActivity, message, Toast.LENGTH_SHORT).show()
        }
        
        viewModel.successMessage.observe(this) { message: String ->
            Toast.makeText(this@ProdukActivity, message, Toast.LENGTH_SHORT).show()
        }
    }
    
    private var lastNotificationTime = mutableMapOf<String, Long>()
    
    private fun checkLowStockNotifications() {
        lifecycleScope.launch {
            viewModel.produkStokMenipis.collect { produkList: List<Produk> ->
                val currentTime = System.currentTimeMillis()
                produkList.forEach { produk: Produk ->
                    if (produk.stok <= 5) {
                        val lastTime = lastNotificationTime[produk.nama] ?: 0
                        // Only notify if last notification was more than 1 hour ago
                        if (currentTime - lastTime > 3600000) {
                            NotificationHelper.showLowStockNotification(
                                this@ProdukActivity,
                                produk.nama,
                                produk.stok
                            )
                            lastNotificationTime[produk.nama] = currentTime
                        }
                    }
                }
            }
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.menu_notifications -> {
                startActivity(Intent(this, NotificationsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }
    
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startBarcodeScanner()
        } else {
            Toast.makeText(this, "Permission kamera diperlukan untuk scan barcode", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun startBarcodeScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Scan barcode produk")
        integrator.setCameraId(0)
        integrator.setBeepEnabled(false)
        integrator.setBarcodeImageEnabled(false)
        integrator.initiateScan()
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Scan dibatalkan", Toast.LENGTH_SHORT).show()
            } else {
                val barcode = result.contents
                viewModel.getProdukByBarcode(
                    barcode,
                    onSuccess = { produk: Produk ->
                        Toast.makeText(this@ProdukActivity, "Produk ditemukan: ${produk.nama}", Toast.LENGTH_SHORT).show()
                        showEditProdukDialog(produk)
                    },
                    onError = {
                        Toast.makeText(this@ProdukActivity, "Produk dengan barcode $barcode tidak ditemukan", Toast.LENGTH_SHORT).show()
                        // Optionally open add dialog with barcode pre-filled
                    }
                )
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
    
    private fun showAddProdukDialog() {
        val dialog = AddEditProdukDialogFragment(null) { produk ->
            viewModel.insertProduk(produk)
            // Show notification for new product
            NotificationHelper.showNotification(
                this,
                "Produk Berhasil Ditambahkan",
                "Produk ${produk.nama} berhasil didaftarkan",
                NotificationHelper.NOTIFICATION_ID_LOW_STOCK + 1,
                "PRODUCT_ADDED"
            )
        }
        dialog.show(supportFragmentManager, "AddProdukDialog")
    }
    
    private fun showEditProdukDialog(produk: Produk) {
        val dialog = AddEditProdukDialogFragment(produk) { updatedProduk ->
            viewModel.updateProduk(updatedProduk)
        }
        dialog.show(supportFragmentManager, "EditProdukDialog")
    }
}

