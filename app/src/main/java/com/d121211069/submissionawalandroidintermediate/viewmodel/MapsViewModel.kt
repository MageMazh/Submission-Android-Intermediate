package com.d121211069.submissionawalandroidintermediate.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.d121211069.submissionawalandroidintermediate.data.remote.response.LoginResult
import com.d121211069.submissionawalandroidintermediate.data.repository.UserRepository

class MapsViewModel(private val repository: UserRepository) : ViewModel() {
    fun getSession(): LiveData<LoginResult> {
        return repository.getSession().asLiveData()
    }

    fun getAllStoriesWithLocation(token: String) = repository.getAllStoriesWithLocation(token)
}