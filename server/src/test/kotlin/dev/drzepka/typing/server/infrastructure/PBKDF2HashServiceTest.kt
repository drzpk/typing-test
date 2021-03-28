package dev.drzepka.typing.server.infrastructure

import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class PBKDF2HashServiceTest {

    @Test
    fun `should compare hashes`() {
        val service = PBKDF2HashService()

        val hash = service.createHash("some password")
        then(service.compareHashes(hash, "some password")).isTrue

        then(service.compareHashes(hash, "different password")).isFalse

        val differentIterationsHash = hash.replace(Regex("^\\d+"), "99")
        then(service.compareHashes(differentIterationsHash, "some password")).isFalse

        val lastPos = hash.length - 1
        val differentHash = hash.replaceRange(lastPos, lastPos + 1, "0")
        then(service.compareHashes(differentHash, "some password"))
    }
}