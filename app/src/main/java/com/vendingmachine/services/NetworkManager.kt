package com.vendingmachine.services


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class NetworkManager {

    companion object {
        private lateinit var baseURL: String
        private var serviceClass: Any? = null

        fun baseURL(baseURL: String) = apply {
            this.baseURL = baseURL
        }

        fun <T> serviceClass(serviceClass: Class<T>?) = apply {
            this.serviceClass = serviceClass
        }

        private val loggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }


        fun <T> create(): T {

            val client = OkHttpClient.Builder().apply {
                addInterceptor(loggingInterceptor)
                connectTimeout(60, TimeUnit.SECONDS)
                readTimeout(60, TimeUnit.SECONDS)
                writeTimeout(60, TimeUnit.SECONDS)
            }.build()



            return Retrofit.Builder()
                .client(client)
                .baseUrl(baseURL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(serviceClass as Class<T>)
        }


    }

}