package com.example.newzz.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newzz.R
import com.example.newzz.adapter.newsadapter
import com.example.newzz.databinding.FragmentSearchBinding
import com.example.newzz.ui.MainActivity
import com.example.newzz.ui.newsviewmodel
import com.example.newzz.utils.const
import com.example.newzz.utils.const.Companion.searchtime
import com.example.newzz.utils.resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class search : Fragment(R.layout.fragment_search) {
    lateinit var newsviewmodel: newsviewmodel
    lateinit var newsadapter: newsadapter
    lateinit var retrybut: Button
    lateinit var error: TextView
    lateinit var itemsearcherror: CardView
    lateinit var binding: FragmentSearchBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentSearchBinding.bind(view)
        val inflater=requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view=inflater.inflate(R.layout.error,null)
        val view1=inflater.inflate(R.layout.fragment_search,null)
        retrybut=view.findViewById(R.id.retryButton)
        error=view.findViewById(R.id.errorText)
        itemsearcherror=view1.findViewById(R.id.itemsearchError)
        newsviewmodel=(activity as MainActivity).Newsviewmodel
        searchrecycler()
        newsadapter.setOnItemClickListener {article->
            val bundle=Bundle().apply{
                putSerializable("article",article)
            }
            findNavController().navigate(
                R.id.action_search2_to_article,
                bundle
            )
        }
        var job: Job?=null
        binding.searchEdit.addTextChangedListener(){editable ->
            job?.cancel()
            job= MainScope().launch {
                delay(searchtime)
                editable?.let{
                    if(editable.toString().isNotEmpty()){
                        newsviewmodel.searchnews(editable.toString())
                    }
                }
            }
        }
        newsviewmodel.searchnews.observe(viewLifecycleOwner, Observer { response->
            when(response){
                is resource.success<*>->{
                    hideprogressbar()
                    hideerror()
                    response.data?.let{newresponse->
                        newsadapter.differ.submitList(newresponse.articles.toList())
                        val pages=newresponse.totalResults/const.pagesize+2
                        islastpage=newsviewmodel.searchnewspage==pages
                        if(islastpage){
                            binding.recyclerSearch.setPadding(0,0,0,0)
                        }
                    }
                }
                is resource.error<*>->{
                    hideprogressbar()
                    response.message?.let{message->
                        Toast.makeText(activity,"an error occured:$message", Toast.LENGTH_LONG).show()
                        showerror(message)
                    }

                }
                is resource.loading<*>->{
                    showprogressbar()
                }
            }
        })
        retrybut.setOnClickListener{
            if(binding.searchEdit.text.toString().isNotEmpty()){
                newsviewmodel.searchnews(binding.searchEdit.text.toString())
            }
            else{
                hideerror()
                Toast.makeText(activity,"Please enter a search query", Toast.LENGTH_LONG).show()
            }
        }
    }
    var iserror=false
    var isloading=false
    var islastpage=false
    var isscrolling=false
    private fun hideprogressbar(){
        binding.paginationProgressBar.visibility=View.INVISIBLE
        isloading=false
    }
    private fun showprogressbar(){
        binding.paginationProgressBar.visibility=View.VISIBLE
        isloading=true
    }
    private fun hideerror(){
        itemsearcherror.visibility=View.INVISIBLE
        iserror=false
    }
    private fun showerror(message: String){
        itemsearcherror.visibility=View.VISIBLE
        error.text=message
        iserror=true
    }
    val scrollListener=object: RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager=recyclerView.layoutManager as LinearLayoutManager
            val firstvisibleitemposition=layoutManager.findFirstVisibleItemPosition()
            val visibleitemcount=layoutManager.childCount
            val totalitemcount=layoutManager.itemCount
            val isnoerrors=!iserror
            val isnotloadingandlastpage=!isloading&&!islastpage
            val isatlastitem=firstvisibleitemposition+visibleitemcount>=totalitemcount
            val isnotatbeggining=firstvisibleitemposition>=0
            val istotalmorethanvisible=totalitemcount>= const.pagesize
            val shouldpaginate=isnoerrors&&isnotloadingandlastpage&&isatlastitem
            if(shouldpaginate){
                newsviewmodel.searchnews(binding.searchEdit.text.toString())
                isscrolling=false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isscrolling=true
            }
        }
    }
    private fun searchrecycler() {
        newsadapter = newsadapter()
        binding.recyclerSearch.apply {
            adapter = newsadapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@search.scrollListener)
        }
    }
}