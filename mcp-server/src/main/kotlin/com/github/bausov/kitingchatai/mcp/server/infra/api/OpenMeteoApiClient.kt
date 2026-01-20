package com.github.bausov.kitingchatai.mcp.server.infra.api

import com.github.bausov.kitingchatai.mcp.server.domain.core.Forecast.UnitTimeStepValues
import com.openmeteo.api.common.Response
import com.openmeteo.api.common.time.Time
import java.time.LocalDateTime
import java.time.ZoneId

abstract class OpenMeteoApiClient {

    @OptIn(Response.ExperimentalGluedUnitTimeStepValues::class)
    protected fun Response.UnitTimeStepValues.toDomain(zoneId: ZoneId): UnitTimeStepValues {
        return UnitTimeStepValues(
            unit = this.unit.toString(),
            values = this.values.mapKeys { (time, _) -> time.toLocalDateTime(zoneId) }
        )
    }

    private fun Time.toLocalDateTime(zoneId: ZoneId): LocalDateTime = this.toInstant().atZone(zoneId).toLocalDateTime()

}