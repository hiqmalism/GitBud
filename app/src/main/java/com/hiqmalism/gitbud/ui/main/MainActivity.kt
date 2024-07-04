package com.hiqmalism.gitbud.ui.main

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.switchmaterial.SwitchMaterial
import com.hiqmalism.gitbud.R
import com.hiqmalism.gitbud.data.response.ItemsItem
import com.hiqmalism.gitbud.databinding.ActivityMainBinding
import com.hiqmalism.gitbud.helper.MainViewModelFactory
import com.hiqmalism.gitbud.helper.SettingPreferences
import com.hiqmalism.gitbud.helper.dataStore
import com.hiqmalism.gitbud.ui.favorite.FavoriteActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = SettingPreferences.getInstance(application.dataStore)

        mainViewModel = ViewModelProvider(this, MainViewModelFactory(pref))[MainViewModel::class.java]

        mainViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView
                .editText
                .setOnEditorActionListener { _, _, _ ->
                    searchView.setupWithSearchBar(searchBar)
                    val username = searchView.text.toString()
                    Log.d("MainActivity", "Searching for: $username")
                    mainViewModel.searchGithub(username)
                    searchView.hide()
                    false
                }
        }

        val layoutManager = GridLayoutManager(this, 2)
        binding.rvAccount.layoutManager = layoutManager

        mainViewModel.listGithub.observe(this) { listGithub ->
            Log.d("MainActivity", "Github list updated: $listGithub")
            setGithubData(listGithub)
        }

        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        val favoriteMenu = menu!!.findItem(R.id.favorite_menu)
        val switchTheme = menu.findItem(R.id.switch_theme)
        val actionView = switchTheme.actionView
        if (actionView is SwitchMaterial) {
            actionView.isChecked = when (AppCompatDelegate.getDefaultNightMode()) {
                AppCompatDelegate.MODE_NIGHT_YES -> true
                else -> false
            }
            actionView.setOnCheckedChangeListener {
                    _: CompoundButton?, isChecked: Boolean ->
                mainViewModel.saveThemeSettings(isChecked)
            }
        }
        favoriteMenu?.icon?.setTint(Color.RED)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.favorite_menu -> {
                val favoriteIntent = Intent(this, FavoriteActivity::class.java)
                startActivity(favoriteIntent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setGithubData(userList: List<ItemsItem>) {
        val adapter = GithubAdapter()
        adapter.submitList(userList)
        binding.rvAccount.adapter = adapter
        Log.d("MainActivity", "Adapter set with data: $userList")
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }
}
