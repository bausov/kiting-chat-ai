package com.github.bausov.kitingchatai.mcp.server.domain.core

import java.time.LocalDateTime

data class ForecastMarineHourly(
    override val currentDateTime: LocalDateTime,
    val waveHeight: UnitTimeStepValues,
    val wavePeriod: UnitTimeStepValues,
    val waveDirection: UnitTimeStepValues
) : Forecast()
