package dev.drzepka.typing.server.domain.entity

import dev.drzepka.typing.server.domain.exception.DomainValidationException
import java.time.Duration
import java.time.Instant

class TestDefinition : AbstractEntity<Int>() {
    var name = ""
    lateinit var wordList: WordList
    var duration: Duration = Duration.ZERO
        set(value) {
            if (value.isZero || value.isNegative)
                throw DomainValidationException("Duration must be a positive value")
            field = value
        }
    var isActive = true
    var createdAt: Instant = Instant.now()
    var modifiedAt: Instant = Instant.now()
    // todo: test order
}