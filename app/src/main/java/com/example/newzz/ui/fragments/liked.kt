package com.example.newzz.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newzz.R
import com.example.newzz.adapter.newsadapter
import com.example.newzz.databinding.FragmentLikedBinding
import com.example.newzz.ui.MainActivity
import com.example.newzz.ui.newsviewmodel
import com.google.android.material.snackbar.Snackbar


class liked : Fragment(R.layout.fragment_liked) {
    lateinit var  newsviewmodel: newsviewmodel
    lateinit var newsadapter: newsadapter
    lateinit var binding: FragmentLikedBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentLikedBinding.bind(view)
        newsviewmodel=(activity as MainActivity).Newsviewmodel
        setrecyclerview()
        newsadapter.setOnItemClickListener {article->
            val bundle=Bundle().apply{
                putSerializable("article",article)
            }
            findNavController().navigate(
                R.id.action_liked_to_article,
                bundle
            )
        }
        val itemTouchHelperCallback=object :ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
         ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position=viewHolder.adapterPosition
                val article=newsadapter.differ.currentList[position]
                newsviewmodel.deletefav(article)
                Snackbar.make(view,"Removed from likes", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        newsviewmodel.addfav(article)
                    }
                    show()
                }
            }

        }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.recyclerFavourites)
        }
        newsviewmodel.getfav().observe(viewLifecycleOwner, Observer { articles ->
            newsadapter.differ.submitList(articles)
        })


    }
    private fun setrecyclerview(){
        newsadapter= newsadapter()
        binding.recyclerFavourites.apply {
            adapter=newsadapter
            layoutManager= LinearLayoutManager(activity)
        }
    }


}