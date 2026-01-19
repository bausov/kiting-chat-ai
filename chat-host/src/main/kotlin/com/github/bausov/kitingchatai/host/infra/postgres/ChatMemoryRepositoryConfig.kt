package com.github.bausov.kitingchatai.host.infra.postgres

import org.springframework.ai.chat.memory.ChatMemoryRepository
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository
import org.springframework.ai.chat.memory.repository.jdbc.PostgresChatMemoryRepositoryDialect
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate

@Configuration
class ChatMemoryRepositoryConfig {

    @Bean
    fun chatMemoryRepository(jdbcTemplate: JdbcTemplate): ChatMemoryRepository {
        return JdbcChatMemoryRepository.builder()
            .dialect(PostgresChatMemoryRepositoryDialect())
            .jdbcTemplate(jdbcTemplate)
            .build()
    }
}