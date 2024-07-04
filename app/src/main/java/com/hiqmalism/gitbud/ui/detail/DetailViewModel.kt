package com.hiqmalism.gitbud.ui.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hiqmalism.gitbud.data.response.DetailUserResponse
import com.hiqmalism.gitbud.data.retrofit.ApiConfig
import com.hiqmalism.gitbud.database.Favorite
import com.hiqmalism.gitbud.repository.FavoriteRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel(application: Application) : ViewModel() {

    private val mFavoriteRepository: FavoriteRepository = FavoriteRepository(application)

    private val _detailUser = MutableLiveData<DetailUserResponse>()
    val detailUser: LiveData<DetailUserResponse> = _detailUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _logMessage = MutableLiveData<String>()
    val logMessage: LiveData<String> = _logMessage

    fun getUserData(username: String) {
        findUser(username)
    }

    fun getFavoriteByUsername(username: String): LiveData<Favorite> = mFavoriteRepository.getFavoriteUserByUsername(username)

    fun insert(favorite: Favorite) {
        mFavoriteRepository.insert(favorite)
    }

    fun delete(favorite: Favorite) {
        mFavoriteRepository.delete(favorite)
    }

    private fun findUser(username: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getDetailUser(username)
        client.enqueue(object : Callback<DetailUserResponse> {
            override fun onResponse(
                call: Call<DetailUserResponse>,
                response: Response<DetailUserResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _detailUser.value = response.body()
                } else {
                    _logMessage.value = "Failed to retrieve user data: ${response.message()}"
                    Log.d("DetailUser", "Ini Kunaon: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailUserResponse>, t: Throwable) {
                _isLoading.value = false
                _logMessage.value = "Error: ${t.message}"
            }
        })
    }
}