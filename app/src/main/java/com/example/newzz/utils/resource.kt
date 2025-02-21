package com.example.newzz.utils

sealed class resource<T>(val data: T? = null, val message: String? = null) {

    class success<T>(data: T) : resource<T>(data)
    class error<T>(message: String, data: T? = null) : resource<T>(data, message)
    class loading<T> : resource<T>()
}
