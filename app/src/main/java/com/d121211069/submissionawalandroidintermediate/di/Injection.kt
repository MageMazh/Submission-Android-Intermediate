package com.d121211069.submissionawalandroidintermediate.di

import android.content.Context
import com.d121211069.submissionawalandroidintermediate.data.local.room.StoryDatabase
import com.d121211069.submissionawalandroidintermediate.data.remote.retrofit.ApiConfig
import com.d121211069.submissionawalandroidintermediate.data.repository.UserRepository
import com.d121211069.submissionawalandroidintermediate.datastore.UserPreferences
import com.d121211069.submissionawalandroidintermediate.datastore.dataStore

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreferences.getInstance(context.dataStore)
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(pref, apiService, database)
    }
}