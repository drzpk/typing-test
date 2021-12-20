package dev.drzepka.typing.server.domain.entity

import dev.drzepka.typing.server.domain.exception.DomainValidationException
import dev.drzepka.typing.server.domain.value.WordListType
import dev.drzepka.typing.server.domain.value.WordSelection
import java.time.Duration
import java.time.Instant

class TestDefinition : AbstractEntity<Int>() {
    var name = ""
    lateinit var wordList: WordList
    /** Test duration. If null, the test will last until all words from [fixed word list](WordListType.FIXED) are typed in. */
    var duration: Duration? = Duration.ZERO
        get() {
            validateDuration(field)
            return field
        }
        set(value) {
            validateDuration(value)
            if (value != null && (value.isZero || value.isNegative))
                throw DomainValidationException("Duration must be a positive value")
            field = value
        }
    var isActive = true
    var createdAt: Instant = Instant.now()
    var modifiedAt: Instant = Instant.now()
    var deletedAt: Instant? = null

    fun getFixedText(): WordSelection? {
        if (wordList.type != WordListType.FIXED)
            return null

        if (wordList.text == null)
            throw IllegalStateException("Word list ${wordList.id} doesn't contain fixed text!")

        return wordList.text
    }

    private fun validateDuration(value: Duration?) {
        if (value == null && wordList.type != WordListType.FIXED)
            throw DomainValidationException("Null duration may only be used with fixed-type word lists.")
    }
}