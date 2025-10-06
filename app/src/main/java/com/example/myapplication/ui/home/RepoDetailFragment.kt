package com.example.myapplication.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.databinding.FragmentRepoDetailBinding
import coil.load

class RepoDetailFragment : Fragment() {

    private var _binding: FragmentRepoDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RepoDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRepoDetailBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[RepoDetailViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.bindFromArguments(requireArguments())

        if (viewModel.urlToImage.isNotEmpty()) {
            binding.image.load(viewModel.urlToImage)
        }
        binding.textFullName.text = viewModel.fullName
        binding.textDescription.text = viewModel.description
        binding.textLanguage.text = viewModel.language
        binding.textStars.text = "★ ${viewModel.stars}"
        binding.textForks.text = "Forks ${viewModel.forks}"

        binding.buttonOpen.setOnClickListener {
            val url = viewModel.url
            if (url.isNotEmpty()) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


