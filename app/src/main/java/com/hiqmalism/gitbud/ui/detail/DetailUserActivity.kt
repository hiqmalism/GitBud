package com.hiqmalism.gitbud.ui.detail

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hiqmalism.gitbud.R
import com.hiqmalism.gitbud.data.response.DetailUserResponse
import com.hiqmalism.gitbud.database.Favorite
import com.hiqmalism.gitbud.databinding.ActivityDetailUserBinding
import com.hiqmalism.gitbud.helper.ViewModelFactory

class DetailUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailUserBinding
    private val detailViewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(application)
    }
    private var favorite: Favorite? = null

    companion object {
        private const val EXTRA_NAME = "username"
        private const val AVATAR_URL = "avatarUrl"
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_followers,
            R.string.tab_following
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "User Page"

        val getIntent = intent.getStringExtra(EXTRA_NAME)
        val getAvatarUrl = intent.getStringExtra(AVATAR_URL)
        val sectionPagerAdapter = SectionPagerAdapter(this).apply {
            username = getIntent.toString()
        }

        detailViewModel.getUserData(getIntent.toString())

        detailViewModel.detailUser.observe(this) { detailUser ->
            setUserData(detailUser)
        }

        detailViewModel.logMessage.observe(this) { errorMessage ->
            errorMessage.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }

        detailViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        val tabs: TabLayout = findViewById(R.id.tabs)

        viewPager.adapter = sectionPagerAdapter
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        binding.fabFavorite.setOnClickListener {

            if (favorite == null) {
                favorite = Favorite().apply {
                    username = getIntent.toString()
                    avatarUrl = getAvatarUrl.toString()
                }
                detailViewModel.insert(favorite as Favorite)
            } else {
                favorite?.let { detailViewModel.delete(it) }
                favorite = null
            }
        }

        detailViewModel.getFavoriteByUsername(getIntent.toString()).observe(this) {
                favoriteUser ->
            if (favoriteUser != null) {
                favorite = favoriteUser
                binding.fabFavorite.setImageResource(R.drawable.ic_favorite)
            } else {
                binding.fabFavorite.setImageResource(R.drawable.ic_unfavorite)
            }
        }
    }

    private fun setUserData(user: DetailUserResponse) {
        with(binding) {
            Glide.with(root.context)
                .load(user.avatarUrl)
                .into(userAvatar)
            tvDisplayName.text = user.name
            tvUsername.text = user.login
            tvFollowers.text = getString(R.string.followers, user.followers)
            tvFollowing.text = getString(R.string.following, user.following)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        }
        else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
    }
}