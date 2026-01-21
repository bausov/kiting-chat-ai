package com.github.bausov.kitingchatai.host.infra.chatmodel

import io.modelcontextprotocol.spec.McpSchema.*
import org.springaicommunity.mcp.annotation.McpSampling
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.ollama.api.OllamaChatOptions
import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ChatModelClientConfig(
    private val chatMemory: ChatMemory,
    private val chatModel: ChatModel,
) {

    @Bean
    fun chatClient(builder: ChatClient.Builder, toolCallbackProvider: ToolCallbackProvider): ChatClient {
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
            .defaultToolCallbacks(toolCallbackProvider)
            .build()
    }

    @McpSampling(clients = ["mcp-server"])
    fun sampling(createMessageRequest: CreateMessageRequest): CreateMessageResult {
        val samplingChatClient = ChatClient.builder(chatModel)
            .defaultOptions(
                OllamaChatOptions.builder()
                    .numPredict(createMessageRequest.maxTokens())
                    .temperature(createMessageRequest.temperature()).build()
            )
            .build()

        val samplingAnswer: String = samplingChatClient
            .prompt()
            .system(createMessageRequest.systemPrompt())
            .user(createMessageRequest.messages().first().content().toString())
            .call()
            .content()!!

        return CreateMessageResult.builder().content(TextContent(samplingAnswer)).build()
    }
}