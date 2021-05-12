package dev.drzepka.typing.server.application.service

interface HashService {
    fun createHash(password: String): String
    fun compareHashes(existingHash: String, password: String): Boolean
}