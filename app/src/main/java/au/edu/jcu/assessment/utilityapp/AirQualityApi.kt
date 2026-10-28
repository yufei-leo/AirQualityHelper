package au.edu.jcu.assessment.utilityapp

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Represents the API response.
data class AirQualityResponse(
    val current: CurrentAirQuality? = null,
    val timezone: String? = null
)

// Values may be missing, so nullable types are used.
data class CurrentAirQuality(
    val time: String? = null,

    @SerializedName("us_aqi")
    val usAqi: Double? = null,

    @SerializedName("pm2_5")
    val pm25: Double? = null,

    val pm10: Double? = null
)

interface AirQualityApi {

    @GET("v1/air-quality")
    suspend fun getAirQuality(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "us_aqi,pm2_5,pm10",
        @Query("timezone") timezone: String = "auto"
    ): AirQualityResponse
}

// Creates a shared API client.
object AirQualityApiClient {

    val api: AirQualityApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://air-quality-api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AirQualityApi::class.java)
    }
}

