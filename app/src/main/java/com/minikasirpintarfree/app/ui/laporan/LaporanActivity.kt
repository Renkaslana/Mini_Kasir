package com.minikasirpintarfree.app.ui.laporan

import android.os.Bundle
import android.widget.Toast
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import kotlinx.coroutines.launch
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.minikasirpintarfree.app.R
import com.minikasirpintarfree.app.data.database.AppDatabase
import com.minikasirpintarfree.app.data.repository.TransaksiRepository
import com.minikasirpintarfree.app.databinding.ActivityLaporanBinding
import com.minikasirpintarfree.app.viewmodel.LaporanViewModel
import com.minikasirpintarfree.app.viewmodel.LaporanViewModelFactory
import com.minikasirpintarfree.app.ui.notifications.NotificationsActivity
import java.text.NumberFormat
import java.util.Locale

class LaporanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLaporanBinding
    private lateinit var viewModel: LaporanViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityLaporanBinding.inflate(layoutInflater)
            setContentView(binding.root)
            
            val database = AppDatabase.getDatabase(this)
            val transaksiRepository = TransaksiRepository(database.transaksiDao())
            viewModel = ViewModelProvider(this, LaporanViewModelFactory(transaksiRepository))[LaporanViewModel::class.java]
            
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            
            setupClickListeners()
            observeViewModel()
            loadDefaultReport()
        } catch (e: Exception) {
            android.util.Log.e("LaporanActivity", "Error in onCreate", e)
            Toast.makeText(this, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
    
    private fun setupClickListeners() {
        binding.btnHariIni.setOnClickListener {
            viewModel.loadTransaksiHariIni()
        }
        
        binding.btnMingguIni.setOnClickListener {
            viewModel.loadTransaksiMingguIni()
        }
        
        binding.btnBulanIni.setOnClickListener {
            viewModel.loadTransaksiBulanIni()
        }
        
        binding.btnExportPdf.setOnClickListener {
            exportReportToPdf()
        }
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.transaksiList.collect { _ ->
                updateStatistics()
                updateChart()
            }
        }
        
        viewModel.selectedPeriod.observe(this) { period ->
            binding.tvSelectedPeriod.text = "Periode: $period"
        }
    }
    
    private fun loadDefaultReport() {
        viewModel.loadTransaksiHariIni()
    }
    
    private fun updateStatistics() {
        val totalPendapatan = viewModel.getTotalPendapatan()
        val totalTransaksi = viewModel.getTotalTransaksi()
        
        binding.tvTotalPendapatan.text = formatCurrency(totalPendapatan)
        binding.tvTotalTransaksi.text = totalTransaksi.toString()
    }
    
    private fun updateChart() {
        val chartData = viewModel.getChartData()
        val entries = chartData.mapIndexed { index, pair ->
            BarEntry(index.toFloat(), pair.second.toFloat())
        }
        
        val dataSet = BarDataSet(entries, "Pendapatan")
        dataSet.color = resources.getColor(R.color.orange, theme)
        
        val barData = BarData(dataSet)
        binding.barChart.data = barData
        binding.barChart.description.text = ""
        binding.barChart.xAxis.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index < chartData.size) chartData[index].first else ""
            }
        }
        binding.barChart.invalidate()
    }
    
    private fun exportReportToPdf() {
        Toast.makeText(this, "Fitur ekspor PDF akan segera tersedia", Toast.LENGTH_SHORT).show()
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
                startActivity(android.content.Intent(this, NotificationsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
}


