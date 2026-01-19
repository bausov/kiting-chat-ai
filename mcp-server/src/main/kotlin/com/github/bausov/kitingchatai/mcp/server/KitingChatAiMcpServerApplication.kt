package com.github.bausov.kitingchatai.mcp.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KitingChatAiMcpServerApplication

fun main(args: Array<String>) {
    runApplication<KitingChatAiMcpServerApplication>(*args)
}
