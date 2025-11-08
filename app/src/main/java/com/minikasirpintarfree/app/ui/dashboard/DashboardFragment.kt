package com.minikasirpintarfree.app.ui.dashboard

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.minikasirpintarfree.app.R
import com.minikasirpintarfree.app.data.database.AppDatabase
import com.minikasirpintarfree.app.data.model.Transaksi
import com.minikasirpintarfree.app.data.model.TransaksiItem
import com.minikasirpintarfree.app.data.repository.ProdukRepository
import com.minikasirpintarfree.app.data.repository.TransaksiRepository
import com.minikasirpintarfree.app.databinding.FragmentDashboardBinding
import com.minikasirpintarfree.app.ui.login.LoginActivity
import com.minikasirpintarfree.app.utils.NotificationHelper
import com.minikasirpintarfree.app.viewmodel.DashboardViewModel
import com.minikasirpintarfree.app.viewmodel.DashboardViewModelFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DashboardViewModel
    private lateinit var bestSellerAdapter: BestSellerAdapter
    private lateinit var recentTransaksiAdapter: RecentTransaksiAdapter
    private val gson = Gson()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            val database = AppDatabase.getDatabase(requireContext())
            val produkRepository = ProdukRepository(database.produkDao())
            val transaksiRepository = TransaksiRepository(database.transaksiDao())
            
            viewModel = ViewModelProvider(
                this,
                DashboardViewModelFactory(produkRepository, transaksiRepository)
            )[DashboardViewModel::class.java]
            
            NotificationHelper.createNotificationChannel(requireContext())
            
            setupGreeting()
            setupRecyclerViews()
            setupClickListeners()
            observeViewModel()
        } catch (e: Exception) {
            android.util.Log.e("DashboardFragment", "Error in onViewCreated", e)
            Toast.makeText(requireContext(), "Terjadi kesalahan: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun setupGreeting() {
        // Get nama toko from SharedPreferences
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val namaToko = prefs.getString("nama_toko", "Mini Kasir Pintar") ?: "Mini Kasir Pintar"
        binding.tvGreeting.text = "Halo, $namaToko 👋"
        
        // Set current date
        val currentDate = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID")).format(java.util.Date())
        binding.tvDate.text = currentDate
    }
    
    private fun setupRecyclerViews() {
        // Setup Best Seller RecyclerView
        bestSellerAdapter = BestSellerAdapter()
        binding.recyclerBestSeller.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bestSellerAdapter
        }
        
        // Setup Recent Transaksi RecyclerView
        recentTransaksiAdapter = RecentTransaksiAdapter { transaksi ->
            showTransaksiDetail(transaksi)
        }
        binding.recyclerRecentTransaksi.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recentTransaksiAdapter
        }
    }
    
    private fun setupClickListeners() {
        // Menu cards removed - navigation now handled by BottomNavigationView only
        // Future feature: Add click listeners for new dashboard features here
    }
    
    private fun showTransaksiDetail(transaksi: Transaksi) {
        try {
            val itemsType = object : TypeToken<List<TransaksiItem>>() {}.type
            val items: List<TransaksiItem> = gson.fromJson(transaksi.items, itemsType)
            
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            val dateFormat = SimpleDateFormat("d MMMM yyyy, HH:mm", Locale("id", "ID"))
            
            val itemsText = items.joinToString("\n") { item ->
                "• ${item.namaProduk} (${item.quantity}x) - ${currencyFormat.format(item.subtotal).replace("Rp", "Rp ")}"
            }
            
            val message = """
                Tanggal: ${dateFormat.format(transaksi.tanggal)}
                
                Item:
                $itemsText
                
                Total: ${currencyFormat.format(transaksi.totalHarga).replace("Rp", "Rp ")}
                Uang Diterima: ${currencyFormat.format(transaksi.uangDiterima).replace("Rp", "Rp ")}
                Kembalian: ${currencyFormat.format(transaksi.kembalian).replace("Rp", "Rp ")}
            """.trimIndent()
            
            AlertDialog.Builder(requireContext())
                .setTitle("Detail Transaksi #TRX-${String.format("%03d", transaksi.id)}")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Gagal memuat detail transaksi", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun observeViewModel() {
        viewModel.totalProduk.observe(viewLifecycleOwner) { total: Int ->
            binding.tvTotalProduk.text = total.toString()
        }
        
        viewModel.totalTransaksiHariIni.observe(viewLifecycleOwner) { total: Int ->
            binding.tvTotalTransaksi.text = total.toString()
        }
        
        viewModel.totalPendapatanHariIni.observe(viewLifecycleOwner) { total: Double ->
            val formattedPendapatan = if (total >= 1000000) {
                String.format("%.1fJt", total / 1000000)
            } else if (total >= 1000) {
                String.format("%.0fK", total / 1000)
            } else {
                String.format("%.0f", total)
            }
            binding.tvTotalPendapatan.text = formattedPendapatan
        }
        
        viewModel.stokMenipis.observe(viewLifecycleOwner) { total: Int ->
            binding.tvStokMenipis.text = total.toString()
            if (total > 0) {
                NotificationHelper.showNotification(
                    requireContext(),
                    "Peringatan Stok",
                    "Ada $total produk dengan stok menipis",
                    NotificationHelper.NOTIFICATION_ID_LOW_STOCK
                )
            }
        }
        
        viewModel.bestSellingProducts.observe(viewLifecycleOwner) { products ->
            if (products.isEmpty()) {
                binding.recyclerBestSeller.visibility = View.GONE
                binding.tvEmptyBestSeller.visibility = View.VISIBLE
            } else {
                binding.recyclerBestSeller.visibility = View.VISIBLE
                binding.tvEmptyBestSeller.visibility = View.GONE
                bestSellerAdapter.submitList(products)
            }
        }
        
        viewModel.recentTransaksi.observe(viewLifecycleOwner) { transaksiList ->
            if (transaksiList.isEmpty()) {
                binding.recyclerRecentTransaksi.visibility = View.GONE
                binding.tvEmptyTransaksi.visibility = View.VISIBLE
            } else {
                binding.recyclerRecentTransaksi.visibility = View.VISIBLE
                binding.tvEmptyTransaksi.visibility = View.GONE
                recentTransaksiAdapter.submitList(transaksiList)
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
