package ru.mirea.vakhrushevra.mireaproject.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface IpWhoService {
    @GET("/")
    suspend fun getIpInfo(): IpInfoResponse
}

interface OpenMeteoService {
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current_weather") currentWeather: Boolean = true
    ): WeatherResponse
}

object NetworkApi {
    private val ipWhoApi: IpWhoService = Retrofit.Builder()
        .baseUrl("https://ipwho.is/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(IpWhoService::class.java)

    private val openMeteoApi: OpenMeteoService = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OpenMeteoService::class.java)

    suspend fun loadNetworkInfo(): NetworkInfo {
        val ipInfo = ipWhoApi.getIpInfo()

        if (ipInfo.success != true) {
            throw IllegalStateException(ipInfo.message ?: "Не удалось получить данные о местоположении")
        }

        val latitude = ipInfo.latitude
            ?: throw IllegalStateException("Координаты недоступны")
        val longitude = ipInfo.longitude
            ?: throw IllegalStateException("Координаты недоступны")

        val weather = openMeteoApi.getWeather(
            latitude = latitude,
            longitude = longitude
        )

        val currentWeather = weather.currentWeather

        return NetworkInfo(
            ip = ipInfo.ip ?: "—",
            city = ipInfo.city ?: "—",
            region = ipInfo.region ?: "—",
            country = ipInfo.country ?: "—",
            temperature = currentWeather?.temperature?.let { "$it °C" } ?: "—",
            windSpeed = currentWeather?.windSpeed?.let { "$it км/ч" } ?: "—",
            weatherDescription = weatherCodeToText(currentWeather?.weatherCode)
        )
    }

    private fun weatherCodeToText(code: Int?): String {
        return when (code) {
            0 -> "Ясно"
            1, 2, 3 -> "Переменная облачность"
            45, 48 -> "Туман"
            51, 53, 55 -> "Морось"
            61, 63, 65 -> "Дождь"
            71, 73, 75 -> "Снег"
            80, 81, 82 -> "Ливень"
            95, 96, 99 -> "Гроза"
            null -> "—"
            else -> "Погода: код $code"
        }
    }
}
