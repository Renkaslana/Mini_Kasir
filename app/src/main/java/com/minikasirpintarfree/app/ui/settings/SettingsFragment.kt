package com.minikasirpintarfree.app.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.minikasirpintarfree.app.R
import com.minikasirpintarfree.app.data.database.AppDatabase
import com.minikasirpintarfree.app.data.repository.ProdukRepository
import com.minikasirpintarfree.app.data.repository.TransaksiRepository
import com.minikasirpintarfree.app.databinding.FragmentSettingsBinding
import com.minikasirpintarfree.app.ui.login.LoginActivity
import com.minikasirpintarfree.app.viewmodel.LoginViewModel
import com.minikasirpintarfree.app.viewmodel.LoginViewModelFactory
import com.minikasirpintarfree.app.viewmodel.SettingsViewModel
import com.minikasirpintarfree.app.viewmodel.SettingsViewModelFactory

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SettingsViewModel
    private lateinit var loginViewModel: LoginViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            val database = AppDatabase.getDatabase(requireContext())
            val produkRepository = ProdukRepository(database.produkDao())
            val transaksiRepository = TransaksiRepository(database.transaksiDao())
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
            
            viewModel = ViewModelProvider(
                this,
                SettingsViewModelFactory(produkRepository, transaksiRepository, sharedPreferences)
            )[SettingsViewModel::class.java]
            
            loginViewModel = ViewModelProvider(
                this,
                LoginViewModelFactory(sharedPreferences)
            )[LoginViewModel::class.java]
            
            setupClickListeners()
            loadCurrentSettings()
        } catch (e: Exception) {
            android.util.Log.e("SettingsFragment", "Error in onViewCreated", e)
            Toast.makeText(requireContext(), "Terjadi kesalahan: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun setupClickListeners() {
        binding.cardTheme.setOnClickListener {
            showThemeDialog()
        }
        
        binding.cardChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }
        
        binding.cardResetProduk.setOnClickListener {
            showResetDialog("Produk") {
                viewModel.resetDataProduk {
                    Toast.makeText(requireContext(), "Data produk telah direset", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        binding.cardResetTransaksi.setOnClickListener {
            showResetDialog("Transaksi") {
                viewModel.resetDataTransaksi {
                    Toast.makeText(requireContext(), "Data transaksi telah direset", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        binding.cardResetAll.setOnClickListener {
            showResetDialog("Semua Data") {
                viewModel.resetAllData {
                    Toast.makeText(requireContext(), "Semua data telah direset", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        binding.cardLogout.setOnClickListener {
            logout()
        }
    }
    
    private fun loadCurrentSettings() {
        binding.switchTheme.isChecked = false
        binding.switchTheme.isEnabled = false
    }
    
    private fun showThemeDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Tema Aplikasi")
            .setMessage("Fitur tema aplikasi sedang dalam pengembangan dan akan segera hadir di versi berikutnya.")
            .setPositiveButton("OK", null)
            .setIcon(android.R.drawable.ic_dialog_info)
            .show()
    }
    
    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val etOldPassword = dialogView.findViewById<android.widget.EditText>(R.id.etOldPassword)
        val etNewPassword = dialogView.findViewById<android.widget.EditText>(R.id.etNewPassword)
        val etConfirmPassword = dialogView.findViewById<android.widget.EditText>(R.id.etConfirmPassword)
        
        AlertDialog.Builder(requireContext())
            .setTitle("Ubah Password")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val oldPassword = etOldPassword.text.toString()
                val newPassword = etNewPassword.text.toString()
                val confirmPassword = etConfirmPassword.text.toString()
                
                if (oldPassword.isEmpty()) {
                    Toast.makeText(requireContext(), "Password lama harus diisi", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                
                if (newPassword.isEmpty()) {
                    Toast.makeText(requireContext(), "Password baru harus diisi", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                
                if (newPassword != confirmPassword) {
                    Toast.makeText(requireContext(), "Password baru tidak cocok", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                
                if (newPassword.length < 6) {
                    Toast.makeText(requireContext(), "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                
                val success = loginViewModel.changePassword(oldPassword, newPassword)
                
                if (success) {
                    Toast.makeText(requireContext(), "Password berhasil diubah", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Password lama salah", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
    
    private fun showResetDialog(dataType: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle("Reset Data")
            .setMessage("Apakah Anda yakin ingin menghapus semua data $dataType? Tindakan ini tidak dapat dibatalkan.")
            .setPositiveButton("Ya, Hapus") { _, _ ->
                onConfirm()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
    
    private fun logout() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin logout?")
            .setPositiveButton("Ya") { _, _ ->
                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
                sharedPreferences.edit().putBoolean("is_logged_in", false).apply()
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
