package com.example.newzz.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.example.newzz.R
import com.example.newzz.databinding.FragmentArticleBinding
import com.example.newzz.ui.MainActivity
import com.example.newzz.ui.newsviewmodel
import com.google.android.material.snackbar.Snackbar

class article : Fragment(R.layout.fragment_article) {
    lateinit var newsviewmodel: newsviewmodel
    val args: articleArgs by navArgs()
    lateinit var binding: FragmentArticleBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArticleBinding.bind(view)
        newsviewmodel = (activity as MainActivity).Newsviewmodel
        val article = args.article

        binding.webView.apply {
            webViewClient = object : WebViewClient() {
                /*override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    Log.d("ArticleFragment", "Page loaded: $url")
                    if (article.content == null) {
                        //extractContent(view)
                    }
                }*/

                /*override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                    super.onReceivedError(view, request, error)
                    Log.e("ArticleFragment", "Error loading page: ${error?.description}")
                    Toast.makeText(context, "Error loading page: ${error?.description}", Toast.LENGTH_LONG).show()
                }*/
            }
            settings.javaScriptEnabled = true
            article.url?.let {
                Log.d("ArticleFragment", "Loading URL: $it")
                loadUrl(it)
            } ?: run {
                Log.e("ArticleFragment", "No URL available")
                Toast.makeText(context, "No URL available", Toast.LENGTH_LONG).show()
            }
        }

        binding.fab.setOnClickListener {
            newsviewmodel.addfav(article)
            Snackbar.make(view, "Added to Favourites", Snackbar.LENGTH_SHORT).show()
        }
    }

    /*private fun extractContent(webView: WebView?) {
        webView?.evaluateJavascript(
            "(function() { return document.body.innerText; })();",
        ) { result ->
            if (result != "null") {
                val content = result.trim('"') // Remove surrounding quotes
                Log.d("ArticleFragment", "Extracted content: $content")
                args.article.content = content.take(500) // Take first 500 characters as a sample
                newsviewmodel.updateArticle(args.article) // Assuming you have this method in your ViewModel
            } else {
                Log.e("ArticleFragment", "Failed to extract content")
            }
        }
    }*/
}