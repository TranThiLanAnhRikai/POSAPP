package com.example.pos.network
import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET


private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
private val moshi = Moshi.Builder()
    .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
    .build()
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface PosApiService {
    @GET("weather?q=Tokyo,jp&units=metric&appid=6a238ea1bff80bfc12ddc7be3d2a0641")
    suspend fun getWeather(): WeatherInfo
}

object PosApi {
    val retrofitService: PosApiService by lazy { retrofit.create(PosApiService::class.java) }
}