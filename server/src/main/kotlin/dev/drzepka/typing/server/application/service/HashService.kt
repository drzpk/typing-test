package dev.drzepka.typing.server.application.service

/**
 * Generates and verifies passwords hashes.
 */
interface HashService {
    /**
     * Generates a hash from given password.
     */
    fun createHash(password: String): String

    /**
     * Compares previously generated hash with given password
     * @param existingHash previously generated hash
     * @param password password to compare
     * @return true is password matches the hash, false otherwise
     */
    fun compareHashes(existingHash: String, password: String): Boolean
}