package com.d121211069.submissionawalandroidintermediate.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.d121211069.submissionawalandroidintermediate.data.remote.response.LoginResult
import com.d121211069.submissionawalandroidintermediate.data.repository.UserRepository
import java.io.File

class UploadViewModel(private val repository: UserRepository) : ViewModel() {
    fun getSession(): LiveData<LoginResult> {
        return repository.getSession().asLiveData()
    }

    fun uploadImage(token: String, file: File, description: String, lat: Float?, long: Float?) =
        repository.uploadImage(token, file, description, lat, long)
}