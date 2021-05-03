package dev.drzepka.typing.server.application.dto

data class BulkImportWordsDTO(val wordListId: Int, val content: String, val errorHandlingMode: ErrorHandlingMode)

enum class ErrorHandlingMode {
    IGNORE, ABORT
}