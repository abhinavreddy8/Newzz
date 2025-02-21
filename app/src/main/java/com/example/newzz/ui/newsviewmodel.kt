package com.example.newzz.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.newzz.model.Article
import com.example.newzz.model.Response

import com.example.newzz.repository.newsrepository
import com.example.newzz.utils.resource
import com.example.newzz.weatherapp
import kotlinx.coroutines.launch
import java.io.IOException
import retrofit2.Response as RetrofitResponse


class newsviewmodel(app: Application,val newsrepository: newsrepository): AndroidViewModel(app) {
    val headlines: MutableLiveData<resource<Response>> = MutableLiveData()
    val searchnews: MutableLiveData<resource<Response>> = MutableLiveData()
    val weather: MutableLiveData<resource<weatherapp>> = MutableLiveData()
    var headlinespage = 1
    var headlinesresponse: Response? = null
    var searchnewspage = 1
    var searchnewsresponse: Response? = null
    var newsearchquery: String? = null
    var oldsearch: String? = null
    init{
        getheadlines("us")
        //getweather("delhi")
    }
    fun getheadlines(country: String) = viewModelScope.launch {
        headlinesinternet(country)
    }
    fun searchnews(searchQuery: String) = viewModelScope.launch {
        searchinternet(searchQuery)
    }
    fun getweather(city:String)=viewModelScope.launch {
        //weatherinternet(city)
    }


    private fun handleresponse(response: RetrofitResponse<Response>): resource<Response> {
        if (response.isSuccessful) {
            response.body()?.let { resultresponse ->

             headlinespage++
             if(headlinesresponse==null){
                 headlinesresponse=resultresponse
             }
                else{
                    val oldarticles=headlinesresponse?.articles
                    val newarticles=resultresponse.articles
                    oldarticles?.addAll(newarticles)
             }
                return resource.success(headlinesresponse ?: resultresponse)
            }
        }
        return resource.error(response.message())
    }
    private fun handlesearchresponse(response: retrofit2.Response<Response>): resource<Response> {
        if(response.isSuccessful){
            response.body()?.let{ resultresponse->
                if(searchnewsresponse==null ||newsearchquery!=oldsearch){
                    searchnewspage=1
                    oldsearch=newsearchquery
                    searchnewsresponse=resultresponse
                }
                else{
                    searchnewspage++
                    val oldarticles=headlinesresponse?.articles
                    val newarticles=resultresponse.articles
                    oldarticles?.addAll(newarticles)
                }
               return resource.success(searchnewsresponse ?: resultresponse)
            }
        }
        return resource.error(response.message())
    }
    fun addfav(article:Article)=viewModelScope.launch {
        newsrepository.insert(article)
    }
    fun getfav()=newsrepository.getsavedarticles()
    fun deletefav(article: Article)=viewModelScope.launch {
        newsrepository.deletearticle(article)
    }
    fun internet(context: Context):Boolean{
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            return getNetworkCapabilities(activeNetwork)?.run{
                when{
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI)->true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)->true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)->true
                    else->false
                }
            }?:false

        }
    }
    private suspend fun headlinesinternet(country: String) {
        headlines.postValue(resource.loading())
        try {
            if (internet(this.getApplication())) {

                    val response = newsrepository.getheadlines(country, headlinespage)
                    headlines.postValue(handleresponse(response))

            } else {
                headlines.postValue(resource.error("no internet connection"))
            }
        }catch (t: Throwable){
            when(t){
                is java.io.IOException->headlines.postValue(resource.error("network failure"))
                else->headlines.postValue(resource.error("conversion error"))
            }
        }

    }
    /*private fun weatherinternet(city: String) {
        weather.postValue(resource.loading())
        try {
            if (internet(getApplication())) {
                Log.i("weather", "weatherinternet called")
                val response = newsrepository.getweather(city, "9c0f9c054b750ef3842f0b3b7989face", "metric")
                Log.i("weather", "response got")
                weather.postValue(handleWeatherResponse(response))
            } else {
                weather.postValue(resource.error("No internet connection"))
            }
        } catch (t: Throwable) {
            weather.postValue(
                when (t) {
                    is IOException -> resource.error("Network failure")
                    else -> resource.error("Conversion error")
                }
            )
        }
    }

    private fun handleWeatherResponse(response: RetrofitResponse<weatherapp>): resource<weatherapp> {
        return if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                resource.success(resultResponse)
            } ?: resource.error("No data found")
        } else {
            resource.error("Error: ${response.message()}")
        }
    }*/

    private suspend fun searchinternet(searchQuery: String){
        newsearchquery=searchQuery
        searchnews.postValue(resource.loading())
        try {
            if (internet(this.getApplication())) {

                    val response = newsrepository.searchnews(searchQuery, searchnewspage)
                    searchnews.postValue(handlesearchresponse(response))

            } else {
                searchnews.postValue(resource.error("no internet connection"))
            }
        }catch (t: Throwable){
            when(t){
                is java.io.IOException->searchnews.postValue(resource.error("network failure"))
                else->searchnews.postValue(resource.error("conversion error"))
            }
        }

    }



}


