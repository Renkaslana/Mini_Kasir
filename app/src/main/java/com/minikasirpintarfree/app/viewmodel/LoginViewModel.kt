package com.minikasirpintarfree.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LoginViewModel(private val sharedPreferences: android.content.SharedPreferences) : ViewModel() {
    
    companion object {
        const val PREF_IS_LOGGED_IN = "is_logged_in"
        const val PREF_USERNAME = "username"
        const val PREF_PASSWORD = "admin123" // Default password
        const val DEFAULT_USERNAME = "admin"
    }
    
    fun login(username: String, password: String): Boolean {
        return if (username == DEFAULT_USERNAME && password == PREF_PASSWORD) {
            viewModelScope.launch {
                sharedPreferences.edit().apply {
                    putBoolean(PREF_IS_LOGGED_IN, true)
                    putString(PREF_USERNAME, username)
                    apply()
                }
            }
            true
        } else {
            false
        }
    }
    
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(PREF_IS_LOGGED_IN, false)
    }
    
    fun logout() {
        viewModelScope.launch {
            sharedPreferences.edit().apply {
                putBoolean(PREF_IS_LOGGED_IN, false)
                apply()
            }
        }
    }
    
    fun changePassword(oldPassword: String, newPassword: String): Boolean {
        return if (oldPassword == PREF_PASSWORD) {
            // Note: In real app, password should be hashed and stored securely
            viewModelScope.launch {
                sharedPreferences.edit().apply {
                    putString("password", newPassword)
                    apply()
                }
            }
            true
        } else {
            false
        }
    }
}

