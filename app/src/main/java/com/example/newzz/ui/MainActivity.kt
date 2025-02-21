package com.example.newzz.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.airbnb.lottie.LottieAnimationView
import com.example.newzz.R
import com.example.newzz.database.articledatabase
import com.example.newzz.databinding.ActivityMainBinding
import com.example.newzz.repository.newsrepository
//9c0f9c054b750ef3842f0b3b7989face
class MainActivity : AppCompatActivity() {
    lateinit var Newsviewmodel: newsviewmodel
    lateinit var binding:ActivityMainBinding
    lateinit var lottieanim:LottieAnimationView
    lateinit var lottieanim1:LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
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
        lottieanim=binding.lottieAnimationView
        lottieanim.setOnClickListener {
            if(internet(this.getApplication())) {
                    val navHostFragment =
                        supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
                    val navController = navHostFragment.navController
                    navController.navigate(R.id.weather)
                }
                else{
                Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG)
                    .show()
            }



        }
        lottieanim1=binding.lottieAnimationView1
        lottieanim1.setOnClickListener{
            val navHostFragment=supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
            val navController=navHostFragment.navController
            navController.navigate(R.id.dictionary)
        }


        val newsrepository = newsrepository(articledatabase(this))
        val viewmodelfactory = newsviewmodelfactory(application, newsrepository)
        Newsviewmodel = ViewModelProvider(this, viewmodelfactory).get(newsviewmodel::class.java)
        //weatherViewModel = ViewModelProvider(this).get(WeatherviewModel::class.java)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            Log.d("NavController", "Navigated to destination: ${destination.label}")
        }

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.headlines -> {
                    // Handle headlines click
                    navController.addOnDestinationChangedListener { controller, destination, arguments ->
                        Log.d("NavController", "Navigated to destination: ${destination.label}")
                    }
                    navController.navigate(R.id.headlines2)
                    animateMenuItem(item)
                    true
                }

                R.id.favourites -> {
                    // Handle favourites click
                    navController.navigate(R.id.liked)
                    animateMenuItem(item)
                    true
                }

                R.id.search -> {
                    // Handle search click
                    navController.navigate(R.id.search2)
                    animateMenuItem(item)
                    true
                }

                else -> false
            }
        }
    }

    private fun animateMenuItem(item: MenuItem) {
        val iconView = binding.bottomNavigationView.findViewById<View>(item.itemId)

        iconView.animate().scaleX(1.4f).scaleY(1.4f).setDuration(200).start()

        // Reset animation after 200ms
        iconView.postDelayed({
            iconView.animate().scaleX(1f).scaleY(1f).setDuration(200).start()
        }, 200)
    }



}
