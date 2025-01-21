package com.example.halfsubmission.data.retrofit

import com.example.halfsubmission.data.response.EventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    fun getActiveEvents(
        @Query("active") active: Int): Call<EventResponse>

    @GET("events")
    fun searchEvents(@Query("active") active: Int, @Query("q") keyword: String): Call<EventResponse>

    @GET("events/{id}")
    fun getEventDetail(@Path("id") id: String): Call<EventResponse>
}