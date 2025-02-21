package com.example.newzz.ui.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.provider.SyncStateContract.Constants
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newzz.R
import com.example.newzz.adapter.newsadapter
import com.example.newzz.databinding.ErrorBinding
import com.example.newzz.databinding.FragmentHeadlinesBinding
import com.example.newzz.model.Article
import com.example.newzz.ui.MainActivity
import com.example.newzz.ui.newsviewmodel
import com.example.newzz.utils.const
import com.example.newzz.utils.resource


class headlines : Fragment(R.layout.fragment_headlines) {
    lateinit var newsviewmodel: newsviewmodel
    lateinit var newsadapter: newsadapter
    lateinit var retrybut: Button
    lateinit var error: TextView
    lateinit var itemheadlineserror: CardView
    lateinit var binding: FragmentHeadlinesBinding
    private lateinit var errorBinding: ErrorBinding
    private lateinit var spinnerCountries: Spinner
    private var selectedCountryCode: String = "in"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHeadlinesBinding.bind(view)
        errorBinding = ErrorBinding.bind(binding.root.findViewById(R.id.itemHeadlinesError))

        //val inflater=requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        //val view=inflater.inflate(R.layout.error,null)
        //val view1=inflater.inflate(R.layout.fragment_headlines,null)
        //retrybut=view.findViewById(R.id.retryButton)
        //error=view.findViewById(R.id.errorText)
        //itemheadlineserror=view1.findViewById(R.id.itemHeadlinesError)
        retrybut = errorBinding.retryButton
        error = errorBinding.errorText
        itemheadlineserror = errorBinding.root

        newsviewmodel = (activity as MainActivity).Newsviewmodel

        headlinesrecycler()
        setupSpinner()

        newsadapter.setOnItemClickListener { article ->
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            findNavController().navigate(
                R.id.action_headlines2_to_article,
                bundle
            )
        }
        newsviewmodel.headlines.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is resource.success<*> -> {
                    hideprogressbar()
                    hideerror()
                    response.data?.let { newresponse ->
                        newsadapter.differ.submitList(newresponse.articles.toList())

                        //binding.recyclerHeadlines.scrollToPosition(0)
                        val pages = newresponse.totalResults / const.pagesize + 2
                        islastpage = newsviewmodel.headlinespage == pages
                        if (islastpage) {
                            binding.recyclerHeadlines.setPadding(0, 0, 0, 0)
                        }
                    }
                }

                is resource.error<*> -> {
                    hideprogressbar()
                    response.message?.let { message ->
                        Toast.makeText(activity, "an error occured :$message", Toast.LENGTH_LONG)
                            .show()
                        showerror(message)
                    }

                }

                is resource.loading<*> -> {
                    showprogressbar()
                }
            }
        })
        retrybut.setOnClickListener {
            if (isInternetAvailable()) {
                hideerror()
                newsviewmodel.getheadlines("us")
            } else {
                showerror("No internet connection")
            }
        }

        if (!isInternetAvailable()) {
            showerror("No internet connection")
        }

    }

    var iserror = false
    var isloading = false
    var islastpage = false
    var isscrolling = false
    private fun hideprogressbar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isloading = false
    }

    private fun showprogressbar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isloading = true
    }

    private fun hideerror() {
        errorBinding.root.visibility = View.INVISIBLE
        errorBinding.retryButton.visibility = View.INVISIBLE
        iserror = false
    }

    private fun showerror(message: String) {
        Log.i("errror", "showerrorlaunche")
        errorBinding.root.visibility = View.VISIBLE
        errorBinding.errorText.text = message
        iserror = true
    }

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstvisibleitemposition = layoutManager.findFirstVisibleItemPosition()
                val visibleitemcount = layoutManager.childCount
                val totalitemcount = layoutManager.itemCount
                val isnoerrors = !iserror
                val isnotloadingandlastpage = !isloading && !islastpage
                val isatlastitem = firstvisibleitemposition + visibleitemcount >= totalitemcount
                val isnotatbeggining = firstvisibleitemposition >= 0
                val istotalmorethanvisible = totalitemcount >= const.pagesize
                val shouldpaginate = isnoerrors && isnotloadingandlastpage && isatlastitem
                if (shouldpaginate) {
                    newsviewmodel.getheadlines("us")
                    isscrolling = false
                }

        }


        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isscrolling = true
            }
        }
    }


    private fun headlinesrecycler() {
        newsadapter = newsadapter()
        binding.recyclerHeadlines.apply {
            adapter = newsadapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@headlines.scrollListener)
        }

    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun setupSpinner() {
        spinnerCountries = binding.spinnerCountries
        val countriesArray = resources.getStringArray(R.array.countries_array)
        val countryCodesArray = resources.getStringArray(R.array.country_codes_array)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, countriesArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCountries.adapter = adapter

        var spinnerInitialized = false

        spinnerCountries.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (!spinnerInitialized) {
                    spinnerInitialized = true
                    return
                }
                selectedCountryCode = countryCodesArray[position]
                newsadapter.differ.submitList(emptyList()) {
                    binding.recyclerHeadlines.scrollToPosition(0)
                }

                newsviewmodel.headlinespage = 1
                newsviewmodel.headlinesresponse = null

                if (isInternetAvailable()) {
                    newsviewmodel.getheadlines(selectedCountryCode)
                } else {
                    showerror("No internet connection")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //binding.recyclerHeadlines.scrollToPosition(0)
                // Optionally handle case where nothing is selected
            }
        }
    }

}