package com.github.bausov.kitingchatai.mcp.server.domain.service

import com.github.bausov.kitingchatai.mcp.server.domain.core.ForecastMarineHourly
import com.github.bausov.kitingchatai.mcp.server.domain.core.ForecastWeatherHourly
import com.github.bausov.kitingchatai.mcp.server.infra.api.OpenMeteoForecastApiClient
import com.github.bausov.kitingchatai.mcp.server.infra.api.OpenMeteoMarineApiClient
import org.springframework.stereotype.Service

@Service
class ForecastService(
    private val weatherForecastProvider: OpenMeteoForecastApiClient,
    private val marineForecastProvider: OpenMeteoMarineApiClient,
) {

    fun getForecast(
        latitude: Float,
        longitude: Float,
        days: Int
    ): Pair<ForecastWeatherHourly, ForecastMarineHourly> {
        return Pair(
            weatherForecastProvider.getWeatherForecast(
                latitude = latitude,
                longitude = longitude,
                days = days
            ),
            marineForecastProvider.getMarineForecast(
                latitude = latitude,
                longitude = longitude,
                days = days
            )
        )
    }
}