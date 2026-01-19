package com.github.bausov.kitingchatai.host.infra.postgres

import com.github.bausov.kitingchatai.host.domain.core.LoadedDocument
import org.springframework.data.jpa.repository.JpaRepository

interface DocumentRepository : JpaRepository<LoadedDocument, Long> {

    fun existsByFilenameAndContentHash(filename: String, contentHash: String): Boolean
}