package com.example.newzz.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.newzz.model.Article

@Database(entities = [Article::class], version = 1)
@TypeConverters(converters::class)
abstract class articledatabase: RoomDatabase() {
    abstract fun dao(): articledao

    companion object {
        @Volatile
        private var instance: articledatabase? = null
        private val lock = Any()
        operator fun invoke(context: Context) = instance ?: synchronized(lock) {
            instance ?: createdatabase(context).also {
                instance = it
            }
        }
        private fun createdatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext, articledatabase::class.java, "articledb.db"
        ).build()
    }
}