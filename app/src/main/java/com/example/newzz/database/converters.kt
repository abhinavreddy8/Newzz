package com.example.newzz.database

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.newzz.model.Source

class converters {
    @TypeConverter
    fun updatesource(source: Source): String? {
        return source.name
    }
    @TypeConverter
    fun retrievesource(name: String): Source {
        return Source(name, name)
    }

}