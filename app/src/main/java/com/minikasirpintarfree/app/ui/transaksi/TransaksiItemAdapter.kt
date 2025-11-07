package com.minikasirpintarfree.app.ui.transaksi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minikasirpintarfree.app.R
import com.minikasirpintarfree.app.data.model.TransaksiItem
import com.minikasirpintarfree.app.databinding.ItemTransaksiCartBinding
import java.text.NumberFormat
import java.util.Locale

class TransaksiItemAdapter(
    private val onQuantityChange: (TransaksiItem, Int) -> Unit,
    private val onItemRemove: (TransaksiItem) -> Unit
) : ListAdapter<TransaksiItem, TransaksiItemAdapter.TransaksiItemViewHolder>(TransaksiItemDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaksiItemViewHolder {
        val binding = ItemTransaksiCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransaksiItemViewHolder(binding, onQuantityChange, onItemRemove)
    }
    
    override fun onBindViewHolder(holder: TransaksiItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class TransaksiItemViewHolder(
        private val binding: ItemTransaksiCartBinding,
        private val onQuantityChange: (TransaksiItem, Int) -> Unit,
        private val onItemRemove: (TransaksiItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: TransaksiItem) {
            binding.tvNamaProduk.text = item.namaProduk
            binding.tvHarga.text = formatCurrency(item.harga)
            binding.tvQuantity.text = item.quantity.toString()
            binding.tvSubtotal.text = formatCurrency(item.subtotal)
            
            binding.btnIncrease.setOnClickListener {
                onQuantityChange(item, item.quantity + 1)
            }
            
            binding.btnDecrease.setOnClickListener {
                if (item.quantity > 1) {
                    onQuantityChange(item, item.quantity - 1)
                }
            }
            
            binding.btnRemove.setOnClickListener {
                onItemRemove(item)
            }
        }
        
        private fun formatCurrency(amount: Double): String {
            val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            return format.format(amount)
        }
    }
    
    class TransaksiItemDiffCallback : DiffUtil.ItemCallback<TransaksiItem>() {
        override fun areItemsTheSame(oldItem: TransaksiItem, newItem: TransaksiItem): Boolean {
            return oldItem.produkId == newItem.produkId
        }
        
        override fun areContentsTheSame(oldItem: TransaksiItem, newItem: TransaksiItem): Boolean {
            return oldItem == newItem
        }
    }
}

