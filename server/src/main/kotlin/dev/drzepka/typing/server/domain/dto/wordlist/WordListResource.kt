package dev.drzepka.typing.server.domain.dto.wordlist

import dev.drzepka.typing.server.domain.entity.WordList

class WordListResource {
    var id = 0
    var name = ""
    var language = ""

    companion object {
        fun fromEntity(entity: WordList): WordListResource {
            return WordListResource().apply {
                id = entity.id.value
                name = entity.name
                language = entity.language.symbol
            }
        }
    }
}