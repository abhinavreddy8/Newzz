package com.example.newzz.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newzz.R
import com.example.newzz.adapter.DictionaryItemAdapter
import com.example.newzz.api.dictionaryinstance
import com.example.newzz.databinding.FragmentDictionaryBinding
import com.example.newzz.model.DictionaryItem
import com.example.newzz.utils.const.Companion.searchtime
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DictionaryFragment : Fragment(R.layout.fragment_dictionary) {
    lateinit var binding: FragmentDictionaryBinding
    private lateinit var dictionaryItemAdapter: DictionaryItemAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDictionaryBinding.bind(view)

        setupRecyclerView()

        var job: Job? = null
        binding.searchEdit.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(searchtime)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        getMeaning(editable.toString())
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        dictionaryItemAdapter = DictionaryItemAdapter()
        binding.meaningRecyclerView.apply {
            adapter = dictionaryItemAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun getMeaning(word: String) {
        GlobalScope.launch {
            val response = dictionaryinstance.api.getMeaning(word)
            response.body()?.let { dictionaryItems ->
                // Update RecyclerView with the new data
                MainScope().launch {
                    setui(dictionaryItems[0])
                    dictionaryItemAdapter.differ.submitList(dictionaryItems)
                }
            }
        }
    }

    private fun setui(dictionaryItem: DictionaryItem) {
        binding.wordTextview.text = dictionaryItem.word
        binding.phoneticTextview.text = dictionaryItem.phonetics[0].text
    }
}
