package com.minikasirpintarfree.app.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minikasirpintarfree.app.R
import com.minikasirpintarfree.app.data.model.Notifikasi
import com.minikasirpintarfree.app.databinding.ItemNotifikasiBinding
import java.text.SimpleDateFormat
import java.util.Locale

class NotifikasiAdapter(
    private val onItemClick: (Notifikasi) -> Unit
) : ListAdapter<Notifikasi, NotifikasiAdapter.NotifikasiViewHolder>(NotifikasiDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifikasiViewHolder {
        val binding = ItemNotifikasiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotifikasiViewHolder(binding, onItemClick)
    }
    
    override fun onBindViewHolder(holder: NotifikasiViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class NotifikasiViewHolder(
        private val binding: ItemNotifikasiBinding,
        private val onItemClick: (Notifikasi) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(notifikasi: Notifikasi) {
            binding.tvTitle.text = notifikasi.title
            binding.tvMessage.text = notifikasi.message
            
            // Format tanggal
            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
            binding.tvDate.text = dateFormat.format(notifikasi.timestamp)
            
            // Set icon berdasarkan type
            val iconRes = when (notifikasi.type) {
                "TRANSACTION" -> R.drawable.ic_notifications
                "LOW_STOCK" -> R.drawable.ic_notifications
                "WEEKLY_REPORT" -> R.drawable.ic_notifications
                else -> R.drawable.ic_notifications
            }
            binding.ivIcon.setImageResource(iconRes)
            
            // Tampilkan badge jika belum dibaca
            binding.viewUnread.visibility = if (notifikasi.isRead) View.GONE else View.VISIBLE
            
            // Set background icon berdasarkan type
            val bgColor = when (notifikasi.type) {
                "TRANSACTION" -> R.color.orange
                "LOW_STOCK" -> android.R.color.holo_red_dark
                "WEEKLY_REPORT" -> android.R.color.holo_blue_dark
                else -> R.color.orange
            }
            binding.viewIconContainer.background = ContextCompat.getDrawable(binding.root.context, bgColor)
            
            binding.root.setOnClickListener {
                onItemClick(notifikasi)
            }
        }
    }
    
    class NotifikasiDiffCallback : DiffUtil.ItemCallback<Notifikasi>() {
        override fun areItemsTheSame(oldItem: Notifikasi, newItem: Notifikasi): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Notifikasi, newItem: Notifikasi): Boolean {
            return oldItem == newItem
        }
    }
}

