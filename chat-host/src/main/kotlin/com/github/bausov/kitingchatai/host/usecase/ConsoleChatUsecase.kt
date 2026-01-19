package com.github.bausov.kitingchatai.host.usecase

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader

@Component
class ConsoleChatUsecase(
    private val chatClient: ChatClient,
) : CommandLineRunner {
    private val chatId = "console-chat-id"
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
                .advisors { spec -> spec.param(CONVERSATION_ID, chatId) }
                .stream()
                .chatResponse()
                .doOnNext { response ->
                    print(response.result.output.text)
                }
                .blockLast()
        }
    }
}