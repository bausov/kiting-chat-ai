package com.github.bausov.kitingchatai.mcp.server.tool

import com.github.bausov.kitingchatai.mcp.server.domain.core.*
import com.github.bausov.kitingchatai.mcp.server.domain.service.ForecastService
import com.github.bausov.kitingchatai.mcp.server.domain.service.SpotService
import io.modelcontextprotocol.spec.McpSchema.*
import org.springaicommunity.mcp.annotation.McpTool
import org.springaicommunity.mcp.annotation.McpToolParam
import org.springaicommunity.mcp.context.McpSyncRequestContext
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class KitingForecastMcpTool(
    private val spotService: SpotService,
    private val forecastService: ForecastService,
) {

    private companion object {
        private val logger by KLogger()
        val SAMPLING_SYSTEM_PROMPT: String = """
            Ты — эксперт по кайтбордингу и кайтсерфингу в Санкт-Петербурге и Ленинградской области.
            Твоя задача — на основе списка спотов и почасовых прогнозов погоды определить,
            КОГДА и ГДЕ реально можно кататься на кайте.

            Ты не фантазируешь и не даёшь общих советов — ты анализируешь только переданные данные.

            Основные критерии пригодности часа для катания:
            1. Ветер:
               - средняя скорость ветра: от 5 до 25 м/с (включительно)
               - порывы ветра не должны сильно превышать среднюю скорость (опасно при сильных порывах)
            2. Направление ветра:
               - направление ветра считается ПРИГОДНЫМ, если оно попадает в допустимый диапазон спота
               - диапазон может переходить через 0° (например 300°–60°)
            3. Светлое время суток:
               - катание возможно ТОЛЬКО между восходом и заходом солнца
            4. Осадки и комфорт:
               - сильные осадки и высокая вероятность осадков ухудшают условия
               - очень низкая температура снижает комфорт, но не является жёстким запретом

            Дополнительные морские условия (если есть):
            - высота и период волны используются как дополнительная информация,
              но не являются строгим ограничением, если ветер подходит

            Результат:
            - если подходящих условий нет — прямо скажи, что катание в ближайшие дни невозможно
            - если условия есть — укажи:
              • спот
              • дату
              • временные интервалы
              • краткое объяснение, ПОЧЕМУ условия подходят
            - если ветер есть, но только ночью или вне допустимого направления — обязательно укажи это
            
            Правило определения подходящего направления ветра:

            Если минимальное направление <= максимального:
            - направление ветра подходит, если оно между min и max включительно

            Если минимальное направление > максимального (диапазон через 0°):
            - направление ветра подходит, если:
              направление >= min ИЛИ направление <= max

            Пример:
            - допустимый диапазон: 300°–60°
            - подходят направления: 350°, 10°, 45°
            - НЕ подходят: 90°, 180°
            """.trimIndent()
    }

    @McpTool(
        name = "KitingForecastMcpTool",
        description = "Используется для получения информации, основанной на прогнозе погоды, о том, когда и где можно будет покататься на кайте."
    )
    fun callKitingForecast(
        requestContext: McpSyncRequestContext,
        @McpToolParam(description = "Количество дней, за которые нужно посмотреть прогноз для разных спотов. Максимальное значение может быть 16, по дефолту 3.")
        days: Int = 3
    ): String {
        val spotsWithForecasts: Map<Spot, Pair<ForecastWeatherHourly, ForecastMarineHourly>> = spotService
            .getAllSpots()
            .associateWith { forecastService.getForecast(it.location.latitude, it.location.latitude, days) }

        val samplingPrompt = spotsWithForecasts.entries
            .joinToString(separator = "\n\n") { (spot, forecasts) ->
                buildSpotForecastPrompt(
                    spot = spot,
                    weather = forecasts.first,
                    marine = forecasts.second
                )
            }

        logger.info("Built sampling prompt for kiting forecast tool: \n{}", samplingPrompt)

        val samplingMessageRequest = CreateMessageRequest.builder()
            .systemPrompt(SAMPLING_SYSTEM_PROMPT)
            .temperature(0.1)
            .maxTokens(50)
            .messages(listOf(SamplingMessage(Role.USER, TextContent(samplingPrompt))))
            .build()

        val samplingResult = requestContext.sample(samplingMessageRequest)

        return samplingResult.content().toString()
    }

    private fun buildSpotForecastPrompt(
        spot: Spot,
        weather: ForecastWeatherHourly,
        marine: ForecastMarineHourly
    ): String {
        val hours: List<LocalDateTime> =
            weather.windSpeed.values.keys
                .sorted()

        return buildString {

            appendLine("СПОТ:")
            appendLine("- название: ${spot.name}")
            appendLine("- координаты: ${spot.location.latitude}, ${spot.location.longitude}")
            appendLine(
                "- допустимое направление ветра: " +
                        "от ${spot.windDirections.min}° до ${spot.windDirections.max}°"
            )
            appendLine()
            appendLine("ПОЧАСОВОЙ ПРОГНОЗ (локальное время):")
            appendLine("Для каждого часа указаны значения:")
            appendLine()

            hours.forEach { time ->
                append(
                    buildHourlyBlock(
                        time = time,
                        weather = weather,
                        marine = marine
                    )
                )
            }
        }
    }

    private fun buildHourlyBlock(
        time: LocalDateTime,
        weather: ForecastWeatherHourly,
        marine: ForecastMarineHourly
    ): String = buildString {

        appendLine("ЧАС: ${time.formatDateTime()}")

        weather.sunrise.valueAt(time)?.let {
            appendLine("- восход солнца: ${time.formatTime()}")
        }

        weather.sunset.valueAt(time)?.let {
            appendLine("- закат солнца: ${time.formatTime()}")
        }

        weather.windSpeed.valueAt(time)?.let {
            appendLine("- скорость ветра: ${"%.1f".format(it)} ${weather.windSpeed.unit}")
        }

        weather.windGusts.valueAt(time)?.let {
            appendLine("- порывы ветра: ${"%.1f".format(it)} ${weather.windGusts.unit}")
        }

        weather.windDirection.valueAt(time)?.let {
            appendLine("- направление ветра: ${it.toInt()}°")
        }

        weather.temperature.valueAt(time)?.let {
            appendLine("- температура воздуха: ${"%.1f".format(it)} ${weather.temperature.unit}")
        }

        weather.apparentTemperature.valueAt(time)?.let {
            appendLine("- ощущается как: ${"%.1f".format(it)} ${weather.apparentTemperature.unit}")
        }

        weather.precipitation.valueAt(time)?.let {
            appendLine("- осадки: ${"%.1f".format(it)} ${weather.precipitation.unit}")
        }

        weather.precipitationProbability.valueAt(time)?.let {
            appendLine("- вероятность осадков: ${it.toInt()}%")
        }

        val waveHeight = marine.waveHeight.valueAt(time)
        val wavePeriod = marine.wavePeriod.valueAt(time)
        val waveDirection = marine.waveDirection.valueAt(time)

        if (waveHeight != null || wavePeriod != null || waveDirection != null) {
            appendLine("- волна (море):")
            waveHeight?.let {
                appendLine("  • высота: ${"%.1f".format(it)} ${marine.waveHeight.unit}")
            }
            wavePeriod?.let {
                appendLine("  • период: ${"%.1f".format(it)} ${marine.wavePeriod.unit}")
            }
            waveDirection?.let {
                appendLine("  • направление: ${it.toInt()}°")
            }
        }

        appendLine()
    }

    private fun Forecast.UnitTimeStepValues.valueAt(time: LocalDateTime): Double? =
        values[time]

    private val DATE_TIME_FORMATTER: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")

    private val TIME_FORMATTER: DateTimeFormatter =
        DateTimeFormatter.ofPattern("HH:mm")

    fun LocalDateTime.formatDateTime(): String =
        this.format(DATE_TIME_FORMATTER)

    fun LocalDateTime.formatTime(): String =
        this.format(TIME_FORMATTER)
}