package com.example.myapplication.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class RepoAdapter(
    private val onClick: (Repo) -> Unit
) : ListAdapter<Repo, RepoViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_repo, parent, false)
        return RepoViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private companion object {
        val DIFF = object : DiffUtil.ItemCallback<Repo>() {
            override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean = oldItem == newItem
        }
    }
}

class RepoViewHolder(
    itemView: View,
    private val onClick: (Repo) -> Unit
) : RecyclerView.ViewHolder(itemView) {
    private val name: TextView = itemView.findViewById(R.id.text_name)
    private val description: TextView = itemView.findViewById(R.id.text_description)
    private val meta: TextView = itemView.findViewById(R.id.text_meta)

    private var current: Repo? = null

    init {
        itemView.setOnClickListener {
            current?.let(onClick)
        }
    }

    fun bind(repo: Repo) {
        current = repo
        name.text = "${repo.owner}/${repo.name}"
        description.text = repo.description
        meta.text = "${repo.language} • ★${repo.stars} • Forks ${repo.forks}"
    }
}


