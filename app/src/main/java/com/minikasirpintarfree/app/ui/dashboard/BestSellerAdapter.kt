package com.minikasirpintarfree.app.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minikasirpintarfree.app.R
import com.minikasirpintarfree.app.data.model.BestSellingProduct
import com.minikasirpintarfree.app.databinding.ItemBestSellerBinding
import java.text.NumberFormat
import java.util.Locale

class BestSellerAdapter : RecyclerView.Adapter<BestSellerAdapter.BestSellerViewHolder>() {
    
    private var products = listOf<BestSellingProduct>()
    
    fun submitList(newList: List<BestSellingProduct>) {
        products = newList
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestSellerViewHolder {
        val binding = ItemBestSellerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BestSellerViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: BestSellerViewHolder, position: Int) {
        holder.bind(products[position], position + 1)
    }
    
    override fun getItemCount() = products.size
    
    class BestSellerViewHolder(
        private val binding: ItemBestSellerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(product: BestSellingProduct, ranking: Int) {
            binding.tvRanking.text = ranking.toString()
            binding.tvNamaProduk.text = product.namaProduk
            binding.tvTotalTerjual.text = "${product.totalTerjual} terjual"
            
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            binding.tvPendapatan.text = currencyFormat.format(product.totalPendapatan)
                .replace("Rp", "Rp ")
        }
    }
}
