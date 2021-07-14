package dev.drzepka.typing.server.domain.value

enum class WordListType(val symbol: String) {
    /** A random text is generated before each test attempt. **/
    RANDOM("R"),

    /** Each test attempt has exactly the same, predefined text. **/
    FIXED("F");

    companion object {
        fun fromSymbol(symbol: String): WordListType = values()
            .firstOrNull { it.symbol == symbol }
            ?: throw NoSuchElementException("No list type with symbol '$symbol' found")
    }
}