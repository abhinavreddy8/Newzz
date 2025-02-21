package com.example.newzz.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.newzz.databinding.DictionaryitemBinding
import com.example.newzz.model.DictionaryItem
import kotlinx.coroutines.Job

class DictionaryItemAdapter : RecyclerView.Adapter<DictionaryItemAdapter.DictionaryViewHolder>() {

    inner class DictionaryViewHolder(val binding: DictionaryitemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var job: Job? = null
    }

    private val differCallback = object : DiffUtil.ItemCallback<DictionaryItem>() {
        override fun areItemsTheSame(oldItem: DictionaryItem, newItem: DictionaryItem): Boolean {
            return oldItem.word == newItem.word
        }

        override fun areContentsTheSame(oldItem: DictionaryItem, newItem: DictionaryItem): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DictionaryViewHolder {
        val binding = DictionaryitemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DictionaryViewHolder(binding)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: DictionaryViewHolder, position: Int) {
        val dictionaryItem = differ.currentList[position]
        holder.binding.apply {
            this.dictionaryItem = dictionaryItem

            // Set the word
            wordTextview.text = dictionaryItem.word

            // Create formatted strings for all meanings
            val fullDefinitionsText = StringBuilder()
            val fullSynonymsText = StringBuilder()
            val fullAntonymsText = StringBuilder()

            dictionaryItem.meanings.forEachIndexed { index, meaning ->
                // Add part of speech
                if (index == 0) {
                    partOfSpeechTextview.text = meaning.partOfSpeech
                } else {
                    fullDefinitionsText.append("\n\n${meaning.partOfSpeech}:")
                }

                // Add definitions
                meaning.definitions.forEachIndexed { defIndex, definition ->
                    fullDefinitionsText.append("\n${defIndex + 1}. ${definition.definition}")
                }

                // Add synonyms if they exist
                if (meaning.synonyms.isNotEmpty()) {
                    if (index > 0) fullSynonymsText.append("\n\n${meaning.partOfSpeech}:")
                    fullSynonymsText.append("\n${meaning.synonyms.joinToString(", ")}")
                }

                // Add antonyms if they exist
                if (meaning.antonyms.isNotEmpty()) {
                    if (index > 0) fullAntonymsText.append("\n\n${meaning.partOfSpeech}:")
                    fullAntonymsText.append("\n${meaning.antonyms.joinToString(", ")}")
                }
            }

            // Set the formatted text to the TextViews
            definitionsTextview.text = fullDefinitionsText.toString().trim()

            // Handle synonyms
            if (fullSynonymsText.isNotEmpty()) {
                synonymsTextview.text = fullSynonymsText.toString().trim()
            } else {
                synonymsTextview.text = "No synonyms available"
            }

            // Handle antonyms
            if (fullAntonymsText.isNotEmpty()) {
                antonymsTextview.text = fullAntonymsText.toString().trim()
            } else {
                antonymsTextview.text = "No antonyms available"
            }
        }
    }
}