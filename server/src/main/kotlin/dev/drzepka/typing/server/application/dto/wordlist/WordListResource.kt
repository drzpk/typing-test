package dev.drzepka.typing.server.application.dto.wordlist

import dev.drzepka.typing.server.domain.entity.WordList

class WordListResource {
    var id = 0
    var name = ""
    var language = ""
    lateinit var type: WordListTypeDTO
    var text: String? = null

    companion object {
        fun fromEntity(entity: WordList): WordListResource {
            return WordListResource().apply {
                id = entity.id!!
                name = entity.name
                language = entity.language.symbol
                type = WordListTypeDTO.fromValue(entity.type)
                text = entity.text?.toText()
            }
        }
    }
}