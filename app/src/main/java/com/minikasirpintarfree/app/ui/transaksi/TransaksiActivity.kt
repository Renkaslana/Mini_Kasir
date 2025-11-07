@file:Suppress("DEPRECATION")

package com.minikasirpintarfree.app.ui.transaksi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.zxing.integration.android.IntentIntegrator
import com.minikasirpintarfree.app.R
import com.minikasirpintarfree.app.data.database.AppDatabase
import com.minikasirpintarfree.app.data.model.Transaksi
import com.minikasirpintarfree.app.data.model.TransaksiItem
import com.minikasirpintarfree.app.data.repository.ProdukRepository
import com.minikasirpintarfree.app.data.repository.TransaksiRepository
import com.minikasirpintarfree.app.databinding.ActivityTransaksiBinding
import com.minikasirpintarfree.app.utils.PdfGenerator
import com.minikasirpintarfree.app.utils.NotificationHelper
import com.minikasirpintarfree.app.ui.notifications.NotificationsActivity
import com.minikasirpintarfree.app.ui.produk.AddEditProdukDialogFragment
import com.minikasirpintarfree.app.viewmodel.TransaksiViewModel
import com.minikasirpintarfree.app.viewmodel.TransaksiViewModelFactory
import java.text.NumberFormat
import java.util.Locale

class TransaksiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTransaksiBinding
    private lateinit var viewModel: TransaksiViewModel
    private lateinit var adapter: TransaksiItemAdapter
    private lateinit var produkRepository: ProdukRepository  // ✅ TAMBAHAN: Untuk digunakan di showAddProdukDialog
    private val CAMERA_PERMISSION_CODE = 100
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityTransaksiBinding.inflate(layoutInflater)
            setContentView(binding.root)
            
            val database = AppDatabase.getDatabase(this)
            produkRepository = ProdukRepository(database.produkDao())  // ✅ TAMBAHAN: Initialize repository
            val transaksiRepository = TransaksiRepository(database.transaksiDao())
            viewModel = ViewModelProvider(this, TransaksiViewModelFactory(transaksiRepository, produkRepository))[TransaksiViewModel::class.java]
            
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            
            NotificationHelper.createNotificationChannel(this)
            
            setupRecyclerView()
            setupClickListeners()
            observeViewModel()
        } catch (e: Exception) {
            android.util.Log.e("TransaksiActivity", "Error in onCreate", e)
            Toast.makeText(this, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
    
    private fun setupRecyclerView() {
        adapter = TransaksiItemAdapter(
            onQuantityChange = { item, newQuantity ->
                viewModel.updateItemQuantity(item, newQuantity)
            },
            onItemRemove = { item ->
                viewModel.removeItemFromCart(item)
            }
        )
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewCart.adapter = adapter
    }
    
    private fun setupClickListeners() {
        binding.btnScanBarcode.setOnClickListener {
            if (checkCameraPermission()) {
                startBarcodeScanner()
            } else {
                requestCameraPermission()
            }
        }
        
        binding.btnSearchProduk.setOnClickListener {
            val query = binding.etSearchProduk.text.toString().trim()
            if (query.isNotEmpty()) {
                searchAndAddProduk(query)
            }
        }
        
        binding.btnBayar.setOnClickListener {
            showPaymentDialog()
        }
        
        binding.btnClearCart.setOnClickListener {
            viewModel.clearCart()
        }
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.cartItems.collect { items: List<TransaksiItem> ->
                adapter.submitList(items)
                binding.tvEmptyCart.visibility = if (items.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
            }
        }
        
        viewModel.totalHarga.observe(this) { total: Double ->
            binding.tvTotalHarga.text = formatCurrency(total)
        }
        
        viewModel.kembalian.observe(this) { kembalian: Double ->
            binding.tvKembalian.text = formatCurrency(kembalian)
        }
        
        viewModel.errorMessage.observe(this) { message: String ->
            Toast.makeText(this@TransaksiActivity, message, Toast.LENGTH_SHORT).show()
        }
        
        viewModel.successMessage.observe(this) { message: String ->
            Toast.makeText(this@TransaksiActivity, message, Toast.LENGTH_SHORT).show()
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
        }
    }
    
    private fun startBarcodeScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Scan barcode produk")
        integrator.setCameraId(0)
        integrator.setBeepEnabled(false)
        integrator.initiateScan()
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && result.contents != null) {
            val barcode = result.contents
            addProdukByBarcode(barcode)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
    
    private fun addProdukByBarcode(barcode: String) {
        val database = com.minikasirpintarfree.app.data.database.AppDatabase.getDatabase(this)
        val produkRepository = com.minikasirpintarfree.app.data.repository.ProdukRepository(database.produkDao())
        
        lifecycleScope.launch {
            val produk = produkRepository.getProdukByBarcode(barcode)
            if (produk != null) {
                val item = com.minikasirpintarfree.app.data.model.TransaksiItem(
                    produkId = produk.id,
                    namaProduk = produk.nama,
                    harga = produk.harga,
                    quantity = 1,
                    subtotal = produk.harga
                )
                viewModel.addItemToCart(item)
                Toast.makeText(this@TransaksiActivity, "Produk ditambahkan: ${produk.nama}", Toast.LENGTH_SHORT).show()
            } else {
                // ✅ FASE 2.1: Ganti Toast dengan AlertDialog
                showProductNotFoundDialog(barcode)
            }
        }
    }
    
    // ✅ FASE 2.1: Fungsi baru untuk show AlertDialog saat produk tidak ditemukan
    private fun showProductNotFoundDialog(barcode: String) {
        AlertDialog.Builder(this)
            .setTitle("Produk Tidak Ditemukan")
            .setMessage("Produk dengan barcode [$barcode] tidak ditemukan.\n\nTambah produk baru dengan barcode ini?")
            .setPositiveButton("Ya") { _, _ ->
                showAddProdukDialog(barcode)
            }
            .setNegativeButton("Tidak", null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }
    
    // ✅ FASE 2.1: Fungsi baru untuk show dialog tambah produk dengan barcode pre-filled
    private fun showAddProdukDialog(barcode: String) {
        val dialog = AddEditProdukDialogFragment(
            produk = null,
            onSave = { newProduk ->
                lifecycleScope.launch {
                    try {
                        produkRepository.insertProduk(newProduk)
                        Toast.makeText(this@TransaksiActivity, "Produk berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                        
                        // Auto-add produk ke keranjang setelah berhasil ditambahkan
                        val item = com.minikasirpintarfree.app.data.model.TransaksiItem(
                            produkId = newProduk.id,
                            namaProduk = newProduk.nama,
                            harga = newProduk.harga,
                            quantity = 1,
                            subtotal = newProduk.harga
                        )
                        viewModel.addItemToCart(item)
                    } catch (e: Exception) {
                        Toast.makeText(this@TransaksiActivity, "Gagal menambahkan produk: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            prefillBarcode = barcode  // ✅ Pass barcode untuk auto-fill
        )
        dialog.show(supportFragmentManager, "AddProdukDialog")
    }
    
    private fun searchAndAddProduk(query: String) {
        val database = com.minikasirpintarfree.app.data.database.AppDatabase.getDatabase(this)
        val produkRepository = com.minikasirpintarfree.app.data.repository.ProdukRepository(database.produkDao())
        
        lifecycleScope.launch {
            val produkList = produkRepository.searchProduk(query).first()
            if (produkList.isNotEmpty()) {
                val produk = produkList[0] // Take first result
                val item = com.minikasirpintarfree.app.data.model.TransaksiItem(
                    produkId = produk.id,
                    namaProduk = produk.nama,
                    harga = produk.harga,
                    quantity = 1,
                    subtotal = produk.harga
                )
                viewModel.addItemToCart(item)
                Toast.makeText(this@TransaksiActivity, "Produk ditambahkan: ${produk.nama}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@TransaksiActivity, "Produk tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun showPaymentDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_payment, null)
        val etUang = dialogView.findViewById<android.widget.EditText>(R.id.etUangDiterima)
        
        AlertDialog.Builder(this)
            .setTitle("Pembayaran")
            .setView(dialogView)
            .setPositiveButton("Bayar") { _, _ ->
                val uang = etUang.text.toString().toDoubleOrNull() ?: 0.0
                viewModel.setUangDiterima(uang)
                viewModel.processTransaksi { transaksi ->
                    NotificationHelper.showTransactionSuccessNotification(this@TransaksiActivity, transaksi.totalHarga)
                    showReceiptDialog(transaksi)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
    
    private fun showReceiptDialog(transaksi: Transaksi) {
        val items = viewModel.getTransaksiItems(transaksi)
        val receiptText = buildReceiptText(transaksi, items)
        
        AlertDialog.Builder(this)
            .setTitle("Struk Transaksi")
            .setMessage(receiptText)
            .setPositiveButton("Simpan PDF") { _, _ ->
                saveReceiptAsPdf(transaksi, items)
            }
            .setNeutralButton("Share") { _, _ ->
                shareReceipt(receiptText)
            }
            .setNegativeButton("Tutup", null)
            .show()
    }
    
    private fun buildReceiptText(transaksi: Transaksi, items: List<TransaksiItem>): String {
        val sb = StringBuilder()
        sb.appendLine("=== STRUK TRANSAKSI ===")
        sb.appendLine("Tanggal: ${android.text.format.DateFormat.format("dd/MM/yyyy HH:mm", transaksi.tanggal)}")
        sb.appendLine("----------------------")
        items.forEach { item ->
            sb.appendLine("${item.namaProduk}")
            sb.appendLine("  ${item.quantity}x ${formatCurrency(item.harga)} = ${formatCurrency(item.subtotal)}")
        }
        sb.appendLine("----------------------")
        sb.appendLine("Total: ${formatCurrency(transaksi.totalHarga)}")
        sb.appendLine("Bayar: ${formatCurrency(transaksi.uangDiterima)}")
        sb.appendLine("Kembali: ${formatCurrency(transaksi.kembalian)}")
        sb.appendLine("======================")
        return sb.toString()
    }
    
    private fun saveReceiptAsPdf(transaksi: Transaksi, items: List<TransaksiItem>) {
        try {
            val pdfPath = PdfGenerator.generateReceipt(this, transaksi, items)
            Toast.makeText(this, "Struk disimpan: $pdfPath", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal menyimpan PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun shareReceipt(receiptText: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, receiptText)
        }
        startActivity(Intent.createChooser(intent, "Bagikan struk"))
    }
    
    private fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return format.format(amount)
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
}
