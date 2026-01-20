package com.github.bausov.kitingchatai.mcp.server.infra.resources

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.bausov.kitingchatai.mcp.server.domain.core.Spot
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component

@Component
class SpotResources(
    private val resourceLoader: ResourceLoader,
    private val objectMapper: ObjectMapper
) {
    private val spots = readJsonList()

    fun getAllSpots(): List<Spot> {
        return spots
    }

    private fun readJsonList(): List<Spot> {
        return resourceLoader
            .getResource("classpath:spots.json")
            .inputStream
            .use { inputStream ->
                objectMapper.readValue(inputStream, object : TypeReference<List<Spot>>() {})
            }
    }
}