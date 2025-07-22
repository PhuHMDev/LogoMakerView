package com.mvvm.esportlogo.extensions

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

inline fun <reified T> T.toJson(): String {
    return Gson().toJson(this)
}

inline fun <reified T> String.fromJson(): T? {
    return try {
        Gson().fromJson(this, T::class.java)
    } catch (e: Exception) {
        null
    }
}

inline fun <reified T> List<T>.toJson(): String {
    return Gson().toJson(this)
}

inline fun <reified T> String.fromJsonList(): List<T>? {
    val type = object : TypeToken<List<T>>() {}.type
    return try {
        Gson().fromJson(this, type)
    } catch (e: Exception) {
        null
    }
}

inline fun <reified T> Context.loadJsonFromAssetsToList(fileName: String): List<T> {
    return try {
        val inputStream = this.assets.open(fileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        val json = String(buffer, Charsets.UTF_8)
        val type = object : TypeToken<List<T>>() {}.type
        Gson().fromJson(json, type) ?: emptyList()
    } catch (e: IOException) {
        e.printStackTrace()
        emptyList()
    }
}

inline fun <reified T> Context.loadJsonFromAssetsToObject(fileName: String): T? {
    return try {
        val inputStream = this.assets.open(fileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        val json = String(buffer, Charsets.UTF_8)
        Gson().fromJson(json, T::class.java)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}