package dev.drzepka.typing.server.domain.value

class WordSelection {
    private val words = ArrayList<String>()

    fun addWord(word: String) {
        assertNoDelimiterIsPresent(word)
        words.add(word)
    }

    fun getWord(index: Int): String = words[index]

    fun size(): Int = words.size

    fun loadFromText(text: String) {
        val parts = text.split(WHITESPACE_SEPARATOR_REGEX)
        parts.forEach {
            assertNoDelimiterIsPresent(it)
        }
        // Adding words must be executed as a separate operation to ensure atomicity
        parts.forEach {
            addWord(it)
        }
    }

    fun toText(): String = words.joinToString(WHITESPACE_SEPARATOR)

    fun serialize(): String = words.joinToString(DELIMITER)

    fun deserialize(input: String): WordSelection {
        words.clear()
        input.split(DELIMITER).forEach { words.add(it) }
        return this
    }

    private fun assertNoDelimiterIsPresent(text: String) {
        if (text.contains(DELIMITER))
            throw IllegalArgumentException("Text '$text' cannot contain the delimiter character ('$DELIMITER')")
    }

    companion object {
        private const val DELIMITER = "|"
        private const val WHITESPACE_SEPARATOR = " "
        private val WHITESPACE_SEPARATOR_REGEX = Regex("\\s+")
    }
}