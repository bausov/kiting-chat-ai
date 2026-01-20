package com.github.bausov.kitingchatai.mcp.server.domain.core

import java.time.LocalDateTime

abstract class Forecast {
    abstract val currentDateTime: LocalDateTime

    data class UnitTimeStepValues(
        val unit: String,
        val values: Map<LocalDateTime, Double?>
    )
}