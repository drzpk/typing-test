package dev.drzepka.typing.server.application.dto.wordlist

import dev.drzepka.typing.server.domain.value.WordListType

enum class WordListTypeDTO {
    RANDOM, FIXED;

    fun toValue(): WordListType = when (this) {
        RANDOM -> WordListType.RANDOM
        FIXED -> WordListType.FIXED
    }

    companion object {
        fun fromValue(value: WordListType): WordListTypeDTO = when (value) {
            WordListType.RANDOM -> RANDOM
            WordListType.FIXED -> FIXED
        }
    }
}