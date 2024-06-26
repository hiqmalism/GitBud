package com.hiqmalism.gitbud.ui.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hiqmalism.gitbud.data.response.ItemsItem
import com.hiqmalism.gitbud.databinding.FragmentFollowBinding

class FollowFragment : Fragment() {

    private lateinit var binding: FragmentFollowBinding
    private var position: Int = 0
    private var username: String? = null

    companion object {
        const val ARG_POSITION = "section_number"
        const val ARG_USERNAME = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFollowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val followViewModel = ViewModelProvider(this)[FollowViewModel::class.java]

        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvFollow.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.rvFollow.addItemDecoration(itemDecoration)

        arguments?.let {
            position = it.getInt(ARG_POSITION)
            username = it.getString(ARG_USERNAME)
        }

        when(position) {
            1 -> {
                followViewModel.getFollowersData(username.toString())
                followViewModel.listFollowers.observe(viewLifecycleOwner) { followerList ->
                    setFollowerData(followerList)
                }
            }

            2 -> {
                followViewModel.getFollowingData(username.toString())
                followViewModel.listFollowing.observe(viewLifecycleOwner) { followingList ->
                    setFollowingData(followingList)
                }
            }
        }

        followViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
    }

    private fun setFollowerData(followerList: List<ItemsItem>) {
        val adapter = FollowAdapter()
        adapter.submitList(followerList)
        binding.rvFollow.adapter = adapter
    }

    private fun setFollowingData(followingList: List<ItemsItem>) {
        val adapter = FollowAdapter()
        adapter.submitList(followingList)
        binding.rvFollow.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}