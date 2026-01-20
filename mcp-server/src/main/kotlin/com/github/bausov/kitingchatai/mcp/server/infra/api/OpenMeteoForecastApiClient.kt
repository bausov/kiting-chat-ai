package com.github.bausov.kitingchatai.mcp.server.infra.api

import com.github.bausov.kitingchatai.mcp.server.domain.core.ForecastWeatherHourly
import com.openmeteo.api.Forecast
import com.openmeteo.api.OpenMeteo
import com.openmeteo.api.common.Response
import com.openmeteo.api.common.time.Timezone
import com.openmeteo.api.common.units.PrecipitationUnit
import com.openmeteo.api.common.units.TemperatureUnit
import com.openmeteo.api.common.units.WindSpeedUnit
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId

@Component
class OpenMeteoForecastApiClient : OpenMeteoApiClient() {

    @OptIn(Response.ExperimentalGluedUnitTimeStepValues::class)
    fun getWeatherForecast(
        latitude: Float,
        longitude: Float,
        days: Int,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): ForecastWeatherHourly {
        val om = OpenMeteo(
            latitude = latitude,
            longitude = longitude
        )

        val forecast = om.forecast {
            daily = Forecast.Daily {
                listOf(
                    sunrise,
                    sunset
                )
            }
            hourly = Forecast.Hourly {
                listOf(
                    windspeed10m,
                    windgusts10m,
                    winddirection10m,
                    temperature2m,
                    apparentTemperature,
                    precipitation,
                    precipitationProbability
                )
            }
            forecastDays = days
            temperatureUnit = TemperatureUnit.Celsius
            windSpeedUnit = WindSpeedUnit.MetresPerSeconds
            precipitationUnit = PrecipitationUnit.Millimeters
            timezone = Timezone.getTimeZone(zoneId)
        }.getOrThrow()

        return ForecastWeatherHourly(
            currentDateTime = LocalDateTime.now(zoneId),
            sunrise = forecast.daily.getValue(Forecast.Daily.sunrise).toDomain(zoneId),
            sunset = forecast.daily.getValue(Forecast.Daily.sunset).toDomain(zoneId),
            windSpeed = forecast.hourly.getValue(Forecast.Hourly.windspeed10m).toDomain(zoneId),
            windGusts = forecast.hourly.getValue(Forecast.Hourly.windgusts10m).toDomain(zoneId),
            windDirection = forecast.hourly.getValue(Forecast.Hourly.winddirection10m).toDomain(zoneId),
            temperature = forecast.hourly.getValue(Forecast.Hourly.temperature2m).toDomain(zoneId),
            apparentTemperature = forecast.hourly.getValue(Forecast.Hourly.apparentTemperature).toDomain(zoneId),
            precipitation = forecast.hourly.getValue(Forecast.Hourly.precipitation).toDomain(zoneId),
            precipitationProbability = forecast.hourly.getValue(Forecast.Hourly.precipitationProbability)
                .toDomain(zoneId)
        )
    }
}
