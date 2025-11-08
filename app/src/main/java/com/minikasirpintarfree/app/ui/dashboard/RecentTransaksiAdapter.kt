package com.minikasirpintarfree.app.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minikasirpintarfree.app.data.model.Transaksi
import com.minikasirpintarfree.app.databinding.ItemRecentTransaksiBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class RecentTransaksiAdapter(
    private val onItemClick: (Transaksi) -> Unit
) : RecyclerView.Adapter<RecentTransaksiAdapter.TransaksiViewHolder>() {
    
    private var transaksiList = listOf<Transaksi>()
    
    fun submitList(newList: List<Transaksi>) {
        transaksiList = newList
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaksiViewHolder {
        val binding = ItemRecentTransaksiBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransaksiViewHolder(binding, onItemClick)
    }
    
    override fun onBindViewHolder(holder: TransaksiViewHolder, position: Int) {
        holder.bind(transaksiList[position])
    }
    
    override fun getItemCount() = transaksiList.size
    
    class TransaksiViewHolder(
        private val binding: ItemRecentTransaksiBinding,
        private val onItemClick: (Transaksi) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(transaksi: Transaksi) {
            binding.tvTransaksiId.text = "#TRX-${String.format("%03d", transaksi.id)}"
            
            val dateFormat = SimpleDateFormat("d MMM yyyy, HH:mm", Locale("id", "ID"))
            binding.tvWaktu.text = dateFormat.format(transaksi.tanggal)
            
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            binding.tvTotal.text = currencyFormat.format(transaksi.totalHarga)
                .replace("Rp", "Rp ")
            
            binding.cardTransaksi.setOnClickListener {
                onItemClick(transaksi)
            }
        }
    }
}
