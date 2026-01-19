package com.github.bausov.kitingchatai.domain.service

import com.github.bausov.kitingchatai.domain.core.KLogger
import com.github.bausov.kitingchatai.domain.core.LoadedDocument
import com.github.bausov.kitingchatai.infra.postgres.DocumentRepository
import jakarta.annotation.PostConstruct
import org.springframework.ai.reader.TextReader
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.core.io.Resource
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.stereotype.Service
import org.springframework.util.DigestUtils

@Service
class DocumentLoaderService(
    private val resourcePatternResolver: ResourcePatternResolver,
    private val documentRepository: DocumentRepository,
    private val vectorStore: VectorStore,
) {

    private companion object {
        private val logger by KLogger()
    }

    @PostConstruct
    fun loadDocuments() {
        logger.info("Starting document loading...")

        resourcePatternResolver.getResources("classpath:/knowledgebase/**/*.txt")
            .map { resource: Resource -> Pair(resource, calcContentHash(resource)) }
            .filter { pair: Pair<Resource, String> ->
                documentRepository.existsByFilenameAndContentHash(pair.first.filename!!, pair.second).not()
            }
            .forEach { pair: Pair<Resource, String> ->
                val resource: Resource = pair.first
                val documents = TextReader(resource).get()
                val textSplitter = TokenTextSplitter.builder().withChunkSize(200).build()
                val chunks = textSplitter.apply(documents)
                vectorStore.accept(chunks)

                val loadedDocument: LoadedDocument = LoadedDocument().apply {
                    filename = resource.filename
                    contentHash = pair.second
                    documentType = "txt"
                    chunkCount = chunks.size
                }

                logger.info("Loaded document: ${loadedDocument.filename}, chunks: ${loadedDocument.chunkCount}")

                documentRepository.save(loadedDocument)
            }

        logger.info("Document loading completed.")
    }

    private fun calcContentHash(resource: Resource): String {
        return DigestUtils.md5DigestAsHex(resource.inputStream)
    }
}