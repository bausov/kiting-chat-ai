package com.github.bausov.kitingchatai.mcp.server.tool

import com.github.bausov.kitingchatai.mcp.server.domain.core.KLogger
import org.springaicommunity.mcp.annotation.McpTool
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Component
class DateTimeMcpTool {

    private companion object {
        private val logger by KLogger()
    }

    @McpTool(
        name = "DateTimeMcpTool",
        description = """
            This tool MUST be used to answer any question about:
            - current time
            - current date
            - today
            - now
            - what time is it
            - what date is it
            The model must not answer such questions without calling this tool.
            
            Returns the current real-world date and time in ISO_LOCAL_DATE_TIME format, day of the week, and day of the year..
        """
    )
    fun callDateTime(): DateTimeMcpToolResponse {
        val dateTime = OffsetDateTime
            .now(ZoneId.of("Europe/Moscow"))
            .toLocalDateTime()
            .truncatedTo(ChronoUnit.SECONDS)

        val dayOfWeek = dateTime.dayOfWeek
        val dayOfYear = dateTime.dayOfYear

        return DateTimeMcpToolResponse(
            dateTime = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            dayOfWeek = dayOfWeek.toString(),
            dayOfYear = dayOfYear,
        )
            .also { logger.info("DateTimeMcpTool called, returning current date-time: $it") }
    }

    data class DateTimeMcpToolResponse(
        val dateTime: String,
        val dayOfWeek: String,
        val dayOfYear: Int,
    )
}