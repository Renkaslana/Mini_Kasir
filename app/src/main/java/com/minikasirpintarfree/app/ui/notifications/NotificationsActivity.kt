package com.minikasirpintarfree.app.ui.notifications

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.minikasirpintarfree.app.R
import com.minikasirpintarfree.app.data.database.AppDatabase
import com.minikasirpintarfree.app.data.repository.NotifikasiRepository
import com.minikasirpintarfree.app.databinding.ActivityNotificationsBinding
import com.minikasirpintarfree.app.viewmodel.NotificationsViewModel
import com.minikasirpintarfree.app.viewmodel.NotificationsViewModelFactory
import kotlinx.coroutines.launch

class NotificationsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationsBinding
    private lateinit var viewModel: NotificationsViewModel
    private lateinit var adapter: NotifikasiAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val database = AppDatabase.getDatabase(this)
        val notifikasiRepository = NotifikasiRepository(database.notifikasiDao())
        viewModel = ViewModelProvider(this, NotificationsViewModelFactory(notifikasiRepository))[NotificationsViewModel::class.java]
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        setupRecyclerView()
        observeViewModel()
    }
    
    private fun setupRecyclerView() {
        adapter = NotifikasiAdapter(
            onItemClick = { notifikasi ->
                lifecycleScope.launch {
                    viewModel.markAsRead(notifikasi.id)
                }
            }
        )
        binding.recyclerViewNotifications.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewNotifications.adapter = adapter
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.notifikasiList.collect { list ->
                adapter.submitList(list)
                binding.tvEmpty.visibility = if (list.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
            }
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_notifications, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.menu_mark_all_read -> {
                lifecycleScope.launch {
                    viewModel.markAllAsRead()
                }
                true
            }
            R.id.menu_clear_all -> {
                lifecycleScope.launch {
                    viewModel.clearAll()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

