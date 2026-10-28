package au.edu.jcu.assessment.utilityapp

data class CityLocation(
    val latitude: Double,
    val longitude: Double
)

class AirQualityRepository(
    private val api: AirQualityApi
) {
    suspend fun getAirQuality(city: String): AirQualityResponse {
        val location = when (city) {
            "Singapore" -> CityLocation(1.3521, 103.8198)
            "Sydney" -> CityLocation(-33.8688, 151.2093)
            "Melbourne" -> CityLocation(-37.8136, 144.9631)
            else -> throw IllegalArgumentException(
                "Unsupported city: $city"
            )
        }

        val response = api.getAirQuality(
            latitude = location.latitude,
            longitude = location.longitude
        )

        checkNotNull(response.current) {
            "Air quality data is unavailable for this city."
        }

        return response
    }
}