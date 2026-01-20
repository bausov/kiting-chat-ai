package com.github.bausov.kitingchatai.mcp.server.domain.core

import java.time.LocalDateTime

data class ForecastWeatherHourly(
    override val currentDateTime: LocalDateTime,
    val sunrise: UnitTimeStepValues,
    val sunset: UnitTimeStepValues,
    val windSpeed: UnitTimeStepValues,
    val windGusts: UnitTimeStepValues,
    val windDirection: UnitTimeStepValues,
    val temperature: UnitTimeStepValues,
    val apparentTemperature: UnitTimeStepValues,
    val precipitation: UnitTimeStepValues,
    val precipitationProbability: UnitTimeStepValues
) : Forecast()
