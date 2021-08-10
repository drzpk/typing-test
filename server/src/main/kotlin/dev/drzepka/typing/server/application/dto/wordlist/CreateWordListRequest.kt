package dev.drzepka.typing.server.application.dto.wordlist

class CreateWordListRequest {
    var name = ""
    var language = ""
    var type: WordListTypeDTO = WordListTypeDTO.RANDOM
    var text: String? = null
}