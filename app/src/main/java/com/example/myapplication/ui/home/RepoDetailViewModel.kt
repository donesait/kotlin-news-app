package com.example.myapplication.ui.home

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RepoDetailViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    var id: Long = 0L
        private set
    var name: String = ""
        private set
    var owner: String = ""
        private set
    var description: String = ""
        private set
    var language: String = ""
        private set
    var stars: Int = 0
        private set
    var forks: Int = 0
        private set
    var url: String = ""
        private set
    var urlToImage: String = ""
        private set

    fun bindFromArguments(args: Bundle) {
        id = args.getLong("id")
        name = args.getString("name").orEmpty()
        owner = args.getString("owner").orEmpty()
        description = args.getString("description").orEmpty()
        language = args.getString("language").orEmpty()
        stars = args.getInt("stars")
        forks = args.getInt("forks")
        url = args.getString("url").orEmpty()
        urlToImage = args.getString("urlToImage").orEmpty()

        savedStateHandle["id"] = id
    }

    val fullName: String get() = "$owner/$name"
}


