package dev.drzepka.typing.server.domain.value

class WordSelection {
    private val words = ArrayList<String>()

    fun addWord(word: String) = words.add(word)

    fun getWord(index: Int): String = words[index]

    fun size(): Int = words.size

    fun serialize(): String = words.joinToString(DELIMITER)

    fun deserialize(input: String) {
        words.clear()
        input.split(DELIMITER).forEach { words.add(it) }
    }

    companion object {
        private const val DELIMITER = "|"
    }
}