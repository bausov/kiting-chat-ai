package com.github.bausov.kitingchatai.mcp.server.infra.api

import com.github.bausov.kitingchatai.mcp.server.domain.core.ForecastMarineHourly
import com.openmeteo.api.Marine
import com.openmeteo.api.OpenMeteo
import com.openmeteo.api.common.Response
import com.openmeteo.api.common.time.Timezone
import com.openmeteo.api.common.units.LengthUnit
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId

@Component
class OpenMeteoMarineApiClient : OpenMeteoApiClient() {

    @OptIn(Response.ExperimentalGluedUnitTimeStepValues::class)
    fun getMarineForecast(
        latitude: Float,
        longitude: Float,
        days: Int,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): ForecastMarineHourly {
        val om = OpenMeteo(
            latitude = latitude,
            longitude = longitude
        )

        val forecast = om.marine {
            hourly = Marine.Hourly {
                listOf(
                    waveHeight,
                    wavePeriod,
                    waveDirection,
                )
            }
            lengthUnit = LengthUnit.Metric
            timezone = Timezone.getTimeZone(zoneId)
        }.getOrThrow()

        return ForecastMarineHourly(
            currentDateTime = LocalDateTime.now(zoneId),
            waveHeight = forecast.hourly.getValue(Marine.Hourly.waveHeight).toDomain(zoneId),
            wavePeriod = forecast.hourly.getValue(Marine.Hourly.wavePeriod).toDomain(zoneId),
            waveDirection = forecast.hourly.getValue(Marine.Hourly.waveDirection).toDomain(zoneId),
        )
    }
}
