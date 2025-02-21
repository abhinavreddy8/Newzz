package com.example.newzz.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import com.example.newzz.R
import com.example.newzz.api.weatherapi
import com.example.newzz.databinding.FragmentWeatherBinding
import com.example.newzz.weatherapp
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class weather : Fragment(R.layout.fragment_weather) {
    lateinit var binding: FragmentWeatherBinding
    private var job: Job? = null
    private val searchtime = 500L // Delay time in milliseconds

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWeatherBinding.bind(view)
        fetchWeather("delhi") // Default city
        setupSearch()
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    fetchWeather(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                job?.cancel()
                job = MainScope().launch {
                    delay(searchtime)
                    newText?.let {
                        if (it.isNotEmpty()) {
                            fetchWeather(it)
                        }
                    }
                }
                return true
            }
        })
    }

    private fun fetchWeather(city: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://api.openweathermap.org/data/2.5/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val weatherApi = retrofit.create(weatherapi::class.java)
                val response = weatherApi.getweather(city, "9c0f9c054b750ef3842f0b3b7989face", "metric").execute()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val responseBody = response.body()!!
                        binding.temperature.text = "${responseBody.main.temp} °C"
                        binding.city.text = responseBody.name
                        binding.min.text = "Min Temp: ${responseBody.main.temp_min} °C"
                        binding.max.text = "Max Temp: ${responseBody.main.temp_max} °C"
                        binding.condition.text = responseBody.weather[0].main
                        binding.humidity.text = responseBody.main.humidity.toString()
                        binding.bbreeze.text = responseBody.wind.speed.toString()
                        binding.cond.text = responseBody.weather[0].description
                        binding.wwindspeed.text = responseBody.wind.speed.toString()

                        // Set date and day
                        val dateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                        val date = Date()
                        binding.day.text = dateFormat.format(date)
                        binding.date.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)
                        changeImage(condition = responseBody.weather[0].main)
                    } else {
                        //Toast.makeText(context, "Failed to fetch weather data", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error fetching weather data", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun changeImage(condition: String) {
        when (condition) {
            "Clear Sky", "Sunny", "Clear" -> {
                binding.lottieAnimationView.setAnimation(R.raw.sun)
                binding.root.setBackgroundResource(R.drawable.sunny_background)
            }

            "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" -> {
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
                binding.root.setBackgroundResource(R.drawable.colud_background)
            }

            "Rain", "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
                binding.lottieAnimationView.setAnimation(R.raw.rain)
                binding.root.setBackgroundResource(R.drawable.rain_background)
            }

            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                binding.lottieAnimationView.setAnimation(R.raw.snow)
                binding.root.setBackgroundResource(R.drawable.snow_background)
            }

            else -> {
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
    }
}
