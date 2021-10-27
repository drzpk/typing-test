package dev.drzepka.typing.server.application.dto.word

class ImportWordsRequest(
    var wordListId: Int,
    var deleteExisting: Boolean,
    val updateExisting: Boolean,
    var words: Collection<WordDTO>
)

data class WordDTO(val word: String, val popularity: Int)