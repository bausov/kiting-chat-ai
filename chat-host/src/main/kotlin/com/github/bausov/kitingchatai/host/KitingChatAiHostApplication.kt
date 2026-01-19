package com.github.bausov.kitingchatai.host

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KitingChatAiHostApplication

fun main(args: Array<String>) {
    runApplication<KitingChatAiHostApplication>(*args)
}
