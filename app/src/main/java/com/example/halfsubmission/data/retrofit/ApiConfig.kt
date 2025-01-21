package com.example.halfsubmission.data.retrofit

import com.example.halfsubmission.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiConfig {
    companion object {
        fun getApiService(): ApiService {
            // Logging interceptor dengan keamanan saat debug
            val loggingInterceptor = HttpLoggingInterceptor().setLevel(
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.NONE
            )

            // OkHttpClient dengan konfigurasi timeout dan interceptor
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            // Retrofit instance dengan BASE_URL dari BuildConfig
            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL) // BASE_URL didefinisikan di build.gradle
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            // Return instance ApiService
            return retrofit.create(ApiService::class.java)
        }
    }
}
