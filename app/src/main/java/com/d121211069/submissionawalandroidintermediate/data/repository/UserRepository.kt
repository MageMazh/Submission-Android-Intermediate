package com.d121211069.submissionawalandroidintermediate.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.*
import com.d121211069.submissionawalandroidintermediate.data.local.entity.StoryEntity
import com.d121211069.submissionawalandroidintermediate.data.local.room.StoryDatabase
import com.d121211069.submissionawalandroidintermediate.data.mediator.StoryRemoteMediator
import com.d121211069.submissionawalandroidintermediate.data.remote.response.LoginResult
import com.d121211069.submissionawalandroidintermediate.data.remote.response.StoryResponse
import com.d121211069.submissionawalandroidintermediate.data.remote.response.UploadResponse
import com.d121211069.submissionawalandroidintermediate.data.remote.retrofit.ApiConfig
import com.d121211069.submissionawalandroidintermediate.data.remote.retrofit.ApiService
import com.d121211069.submissionawalandroidintermediate.datastore.UserPreferences
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.d121211069.submissionawalandroidintermediate.util.Result
import com.d121211069.submissionawalandroidintermediate.util.wrapEspressoIdlingResource
import retrofit2.HttpException
import java.io.File

class UserRepository private constructor(
    private val userPreference: UserPreferences,
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {

    fun signupUser(name: String, email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val successResponse = ApiConfig.getApiService().register(name, email, password)
            emit(Result.Success(successResponse))

        } catch (e: Exception) {
            Log.d("UserRepository", "signupUser: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun loginUser(email: String, password: String) = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val successResponse = ApiConfig.getApiService().login(email, password)
                emit(Result.Success(successResponse))

            } catch (e: Exception) {
                Log.d("UserRepository", "loginUser: ${e.message.toString()} ")
                emit(Result.Error(e.message.toString()))
            }
        }
    }

    suspend fun saveSession(user: LoginResult) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<LoginResult> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun getAllStories(token: String): LiveData<PagingData<StoryEntity>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    fun getAllStoriesWithLocation(token: String): LiveData<Result<StoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val successResponse = ApiConfig.getApiService().getStoriesWithLocation("Bearer $token")
            emit(Result.Success(successResponse))
        } catch (e: Exception) {
            Log.d("UserRepository", "getAllStoriesWithLocation: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun uploadImage(
        token: String,
        imageFile: File,
        description: String,
        lat: Float?,
        long: Float?
    ) = liveData {
        emit(Result.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestLat = lat?.toString()?.toRequestBody("text/plain".toMediaType())
        val requestLong = long?.toString()?.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())

        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val successResponse =
                ApiConfig.getApiService().uploadImage(
                    "Bearer $token",
                    multipartBody,
                    requestBody,
                    requestLat,
                    requestLong
                )

            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, UploadResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreferences,
            apiService: ApiService,
            database: StoryDatabase
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, database, apiService)
            }.also { instance = it }
    }
}