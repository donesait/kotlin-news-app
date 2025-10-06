package com.example.myapplication.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.R
import kotlinx.coroutines.launch
import com.google.android.material.textfield.TextInputEditText
import android.view.inputmethod.EditorInfo

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Static top padding is set in layout to avoid inconsistent insets after back navigation

        val adapter = RepoAdapter { repo ->
            findNavController().navigate(
                R.id.action_navigation_home_to_repoDetailFragment,
                Bundle().apply {
                    putLong("id", repo.id)
                    putString("name", repo.name)
                    putString("owner", repo.owner)
                    putString("description", repo.description)
                    putString("language", repo.language)
                    putInt("stars", repo.stars)
                    putInt("forks", repo.forks)
                    putString("url", repo.url)
                    putString("urlToImage", repo.urlToImage)
                }
            )
        }

        binding.recyclerRepos.adapter = adapter
        binding.recyclerRepos.addItemDecoration(
            DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        )

        homeViewModel.repos.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.emptyView.visibility = if (list.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
        homeViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        binding.editQuery.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                homeViewModel.updateQuery(v.text?.toString().orEmpty())
                homeViewModel.search()
                true
            } else false
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}