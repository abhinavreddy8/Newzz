package com.example.newzz.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newzz.R
import com.example.newzz.databinding.NewsitemBinding
import com.example.newzz.model.Article
import kotlinx.coroutines.*
import org.jsoup.Jsoup

class newsadapter : RecyclerView.Adapter<newsadapter.articleviewholder>() {

    inner class articleviewholder(val binding: NewsitemBinding) : RecyclerView.ViewHolder(binding.root) {
        var job: Job? = null
    }

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): articleviewholder {
        val binding: NewsitemBinding = NewsitemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return articleviewholder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: articleviewholder, position: Int) {
        val article = differ.currentList[position]
        holder.binding.article = article

        // Cancel any ongoing job for this ViewHolder
        holder.job?.cancel()

        // Load image
        loadImage(holder, article)

        // Set initial description
        holder.binding.articleDescription.text = article.description ?: article.content ?: "Loading description..."

        // Check if description or content is null
        if (article.description.isNullOrBlank() && article.content.isNullOrBlank()) {
            fetchContentAndImage(holder, article, position)
        }

        holder.binding.executePendingBindings()

        holder.itemView.setOnClickListener {
            Log.d("NewsAdapter", "Article clicked: ${article.url}")
            onItemClickListener?.let { it(article) }
        }
    }

    private fun loadImage(holder: articleviewholder, article: Article) {
        if (article.urlToImage != null) {
            Glide.with(holder.itemView.context)
                .load(article.urlToImage)
                .placeholder(R.drawable.googlenews)
                .error(R.drawable.googlenews)
                .into(holder.binding.articleImage)
        } else {
            Glide.with(holder.itemView.context)
                .load(R.drawable.googlenews)
                .into(holder.binding.articleImage)
        }
    }

    private fun fetchContentAndImage(holder: articleviewholder, article: Article, position: Int) {
        holder.job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val doc = Jsoup.connect(article.url).get()
                val description = doc.select("meta[name=description]").attr("content")
                val content = doc.select("p").take(2).joinToString("\n") { it.text() }

                // Fetch image if urlToImage is null
                val imageUrl = if (article.urlToImage == null) {
                    doc.select("meta[property=og:image]").attr("content")
                        .ifEmpty { doc.select("img[src~=(?i)\\.(png|jpe?g)]").firstOrNull()?.attr("src") }
                } else null

                // Update the UI on the main thread
                withContext(Dispatchers.Main) {
                    // Check if the ViewHolder is still showing the same article
                    if (holder.adapterPosition == position && holder.adapterPosition != RecyclerView.NO_POSITION) {
                        // Update description
                        when {
                            description.isNotBlank() -> holder.binding.articleDescription.text = description
                            content.isNotBlank() -> holder.binding.articleDescription.text = content
                            else -> holder.binding.articleDescription.text = "No description available"
                        }

                        // Update image if a new one was found
                        if (imageUrl != null) {
                            Glide.with(holder.itemView.context)
                                .load(imageUrl)
                                .placeholder(R.drawable.googlenews)
                                .error(R.drawable.googlenews)
                                .into(holder.binding.articleImage)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("NewsAdapter", "Error fetching content: ${e.message}")
                // Update UI to show error message
                withContext(Dispatchers.Main) {
                    if (holder.adapterPosition == position && holder.adapterPosition != RecyclerView.NO_POSITION) {
                        holder.binding.articleDescription.text = "Unable to fetch description"
                    }
                }
            }
        }
    }

    override fun onViewRecycled(holder: articleviewholder) {
        super.onViewRecycled(holder)
        holder.job?.cancel()
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }
}