package dev.drzepka.typing.server.domain.exception

class FixedTextTooShortException(
    testDefinitionId: Int,
    wordListId: Int,
    length: Int,
    minimumLength: Int
) :
    RuntimeException("Fixed word list $wordListId is too short for test definition $testDefinitionId. Minimum length is $minimumLength (current length: $length)")