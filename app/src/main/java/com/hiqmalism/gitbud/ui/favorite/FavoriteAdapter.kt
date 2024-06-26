package com.hiqmalism.gitbud.ui.favorite

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hiqmalism.gitbud.database.Favorite
import com.hiqmalism.gitbud.databinding.ItemFavoriteBinding
import com.hiqmalism.gitbud.helper.FavoriteDiffCallback
import com.hiqmalism.gitbud.ui.detail.DetailUserActivity
import com.hiqmalism.gitbud.ui.favorite.FavoriteAdapter.FavoriteViewHolder

class FavoriteAdapter : RecyclerView.Adapter<FavoriteViewHolder>() {
    private val listFavorite = ArrayList<Favorite>()
    fun setListFav(listFavorite: List<Favorite>) {
        val diffCallback = FavoriteDiffCallback(this.listFavorite, listFavorite)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listFavorite.clear()
        this.listFavorite.addAll(listFavorite)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(listFavorite[position])
    }

    override fun getItemCount(): Int {
        return listFavorite.size
    }

    inner class FavoriteViewHolder(private val binding: ItemFavoriteBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val favorite = listFavorite[position]
                    val intent = Intent(binding.root.context, DetailUserActivity::class.java).apply {
                        putExtra("username", favorite.username)
                    }
                    binding.root.context.startActivity(intent)
                }
            }
        }

        fun bind(favorite: Favorite) {
            with(binding) {
                tvItemUsername.text = favorite.username
                Glide.with(binding.root.context)
                    .load(favorite.avatarUrl)
                    .into(binding.imgItemAvatar)
            }
        }
    }
}