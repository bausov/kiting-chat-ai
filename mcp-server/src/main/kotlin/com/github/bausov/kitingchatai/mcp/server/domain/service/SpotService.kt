package com.github.bausov.kitingchatai.mcp.server.domain.service

import com.github.bausov.kitingchatai.mcp.server.infra.resources.SpotResources
import org.springframework.stereotype.Service

@Service
class SpotService(
    private val spotResources: SpotResources
) {

    fun getAllSpots() = spotResources.getAllSpots()
}