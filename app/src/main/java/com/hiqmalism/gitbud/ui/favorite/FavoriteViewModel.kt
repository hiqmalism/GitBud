package com.hiqmalism.gitbud.ui.favorite

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hiqmalism.gitbud.database.Favorite
import com.hiqmalism.gitbud.repository.FavoriteRepository

class FavoriteViewModel(application: Application) : ViewModel() {
    private val mFavoriteRepository: FavoriteRepository = FavoriteRepository(application)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getAllFavorites(): LiveData<List<Favorite>> {
        _isLoading.value = true
        return mFavoriteRepository.getAllFavorites().apply {
            _isLoading.value = false
        }
    }
}