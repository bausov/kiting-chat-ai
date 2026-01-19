package com.github.bausov.kitingchatai.infra.chatmodel

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.ollama.api.OllamaChatOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ChatModelClientConfig(
    private val chatMemory: ChatMemory,
) {

    @Bean
    fun chatClient(builder: ChatClient.Builder): ChatClient {
        return builder
            .defaultOptions(
                OllamaChatOptions.builder()
                    .temperature(0.3)
                    .topP(0.7)
                    .topK(20)
                    .repeatPenalty(1.1)
                    .build()
            )
            .defaultAdvisors(
                MessageChatMemoryAdvisor.builder(chatMemory).order(0).build()
            )
            .build()
    }
}