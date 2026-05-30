package ru.mirea.vakhrushevra.mireaproject.network

import com.google.gson.annotations.SerializedName

data class IpInfoResponse(
    @SerializedName("success") val success: Boolean?,
    @SerializedName("ip") val ip: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("region") val region: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("message") val message: String?
)

data class WeatherResponse(
    @SerializedName("current_weather") val currentWeather: CurrentWeather?
)

data class CurrentWeather(
    @SerializedName("temperature") val temperature: Double?,
    @SerializedName("windspeed") val windSpeed: Double?,
    @SerializedName("weathercode") val weatherCode: Int?
)

data class NetworkInfo(
    val ip: String,
    val city: String,
    val region: String,
    val country: String,
    val temperature: String,
    val windSpeed: String,
    val weatherDescription: String
)
