package dev.drzepka.typing.server.domain.service

interface HashService {
    fun createHash(password: String): String
    fun compareHashes(existingHash: String, password: String): Boolean
}