package com.example.newzz.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newzz.repository.newsrepository

class newsviewmodelfactory (val app: Application, val newsrepository: newsrepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return newsviewmodel(app,newsrepository) as T
    }

}