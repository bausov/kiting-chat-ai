package com.github.bausov.kitingchatai.usecase

import org.springframework.ai.chat.client.ChatClient
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader

@Component
class ConsoleChatUsecase(
    private val chatClient: ChatClient,
) : CommandLineRunner {
    private val reader = BufferedReader(InputStreamReader(System.`in`))

    override fun run(vararg args: String?) {
        println("Welcome to Kiting Chat AI Console!")

        while (true) {
            print("Q: ")

            val input = reader.readLine()

            if ("exit".equals(input, ignoreCase = true)) {
                break
            }

            val answer = chatClient.prompt().user(input).call().content()

            println("A: $answer")
        }
    }
}