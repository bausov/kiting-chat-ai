package com.github.bausov.kitingchatai.domain.core

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

@Entity
data class LoadedDocument(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,
    var filename: String?,
    var contentHash: String?,
    var documentType: String?,
    var chunkCount: Int?,
    @CreationTimestamp
    var loadedAt: Instant?,
) {
    constructor() : this(null, null, null, null, null, null)
}
