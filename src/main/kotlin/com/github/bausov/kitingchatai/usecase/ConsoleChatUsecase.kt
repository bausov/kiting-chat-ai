package com.github.bausov.kitingchatai.usecase

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader

@Component
class ConsoleChatUsecase(
    private val chatClient: ChatClient,
    private val chatMemory: ChatMemory,
) : CommandLineRunner {
    private val chatId = "console-chat"
    private val reader = BufferedReader(InputStreamReader(System.`in`))

    override fun run(vararg args: String?) {
        println("Welcome to Kiting Chat AI Console!")

        while (true) {
            print("Q: ")

            val input = reader.readLine()

            if ("exit".equals(input, ignoreCase = true)) {
                break
            }

            print("A: ")

            chatClient
                .prompt(input)
                .advisors(
                    MessageChatMemoryAdvisor.builder(chatMemory).conversationId(chatId).order(0).build()
                )
                .stream()
                .chatResponse()
                .doOnNext { response ->
                    print(response.result.output.text)
                }
                .blockLast()
        }
    }
}