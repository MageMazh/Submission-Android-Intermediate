package com.d121211069.submissionawalandroidintermediate.viewmodel

import androidx.lifecycle.ViewModel
import com.d121211069.submissionawalandroidintermediate.data.repository.UserRepository

class SignupViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun signupUser(name: String, email: String, password: String) =
        userRepository.signupUser(name, email, password)
}
