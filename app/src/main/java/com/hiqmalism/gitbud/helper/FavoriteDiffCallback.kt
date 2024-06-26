package com.hiqmalism.gitbud.helper

import androidx.recyclerview.widget.DiffUtil
import com.hiqmalism.gitbud.database.Favorite

class FavoriteDiffCallback(private val oldFavList: List<Favorite>, private val newFavList: List<Favorite>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldFavList.size
    override fun getNewListSize(): Int = newFavList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldFavList[oldItemPosition].username == newFavList[newItemPosition].username
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldFavorite = oldFavList[oldItemPosition]
        val newFavorite = newFavList[newItemPosition]
        return oldFavorite.username == newFavorite.username && oldFavorite.avatarUrl == newFavorite.avatarUrl
    }
}