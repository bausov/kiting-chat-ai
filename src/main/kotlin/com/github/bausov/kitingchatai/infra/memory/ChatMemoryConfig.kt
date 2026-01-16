package com.github.bausov.kitingchatai.infra.memory

import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ChatMemoryConfig(
    @Value("\${chat.memory.max-messages:5}")
    private val maxMessages: Int
) {

    @Bean
    fun chatMemory(): ChatMemory {
        return MessageWindowChatMemory.builder()
            .maxMessages(maxMessages)
            .build()
    }
}