package dev.drzepka.typing.server.application.dto.word

import dev.drzepka.typing.server.domain.entity.Word

class WordResource {
    var id = 0
    var word = ""
    var popularity = 0
    var wordListId = 0

    companion object {
        fun fromEntity(word: Word): WordResource {
            return WordResource().apply {
                id = word.id!!
                this.word = word.word
                popularity = word.popularity
                wordListId = word.wordListId
            }
        }
    }
}