package dev.drzepka.typing.server.domain.exception

class FixedTextTooShortException(
    val testDefinition: String,
    val wordListId: Int,
    val length: Int,
    val minimumLength: Int
) :
    RuntimeException("Fixed word list $wordListId is too short for test definition $testDefinition. Minimum length is $minimumLength (current length: $length)")