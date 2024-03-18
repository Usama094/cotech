package com.example.myapplication

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("find_places")
    suspend fun getWeather(
        @Query("text") location: String,
        @Query("language") language: String,
        @Query("X-RapidAPI-Key") apiKey: String
    ): Response<WeatherResponse>
}
