package com.d121211069.submissionawalandroidintermediate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d121211069.submissionawalandroidintermediate.data.remote.response.LoginResult
import com.d121211069.submissionawalandroidintermediate.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun saveSession(user: LoginResult) {
        viewModelScope.launch {
            userRepository.saveSession(user)
        }
    }

    fun loginUser(email: String, password: String) = userRepository.loginUser(email, password)
}