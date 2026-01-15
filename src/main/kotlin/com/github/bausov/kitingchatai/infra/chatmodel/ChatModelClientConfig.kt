package com.github.bausov.kitingchatai.infra.chatmodel

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.ollama.api.OllamaChatOptions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ChatModelClientConfig {

    @Autowired
    private lateinit var chatModel: ChatModel

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
            .build()
    }
}