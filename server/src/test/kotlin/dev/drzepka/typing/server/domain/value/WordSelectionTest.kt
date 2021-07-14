package dev.drzepka.typing.server.domain.value

import org.assertj.core.api.BDDAssertions.assertThatIllegalArgumentException
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

internal class WordSelectionTest {

    @Test
    fun `should store words`() {
        val selection = WordSelection()
        selection.addWord("word1")
        selection.addWord("word2")
    }

    @Test
    fun `should not store words containing delimiter`() {
        val selection = WordSelection()

        selection.addWord("word1")
        then(selection.size()).isEqualTo(1)
        then(selection.getWord(0)).isEqualTo("word1")

        assertThatIllegalArgumentException().isThrownBy {
            selection.addWord("word2$DELIMITER")
        }.withMessage("Text 'word2$DELIMITER' cannot contain the delimiter character ('$DELIMITER')")
        then(selection.size()).isEqualTo(1)
    }

    @Test
    fun `should serialize word selection`() {
        val selection = WordSelection()
        selection.addWord("first")
        selection.addWord("second")
        selection.addWord("THIRD")

        val serialized = selection.serialize()
        then(serialized).isEqualTo("first${DELIMITER}second${DELIMITER}THIRD")
    }

    @Test
    fun `should deserialize word selection`() {
        val selection = WordSelection()
        selection.deserialize("abc${DELIMITER}DEF${DELIMITER}ghi")

        then(selection.size()).isEqualTo(3)
        then(selection.getWord(0)).isEqualTo("abc")
        then(selection.getWord(1)).isEqualTo("DEF")
        then(selection.getWord(2)).isEqualTo("ghi")
    }

    @Test
    fun `should load words from text`() {
        val longText = """
            This is
            an, example
               multiline     string.
        """.trimIndent()

        val selection = WordSelection()
        selection.loadFromText(longText)

        then(selection.size()).isEqualTo(6)
        then(selection.getWord(0)).isEqualTo("This")
        then(selection.getWord(1)).isEqualTo("is")
        then(selection.getWord(2)).isEqualTo("an,")
        then(selection.getWord(3)).isEqualTo("example")
        then(selection.getWord(4)).isEqualTo("multiline")
        then(selection.getWord(5)).isEqualTo("string.")
    }

    @Test
    fun `should not load words from text with delimiter - atomic operation`() {
        val invalidLongText = "This is text with$DELIMITER delimiters.$DELIMITER"

        val selection = WordSelection()
        selection.addWord("initial")
        assertThatIllegalArgumentException().isThrownBy {
            selection.loadFromText(invalidLongText)
        }.withMessage("Text 'with$DELIMITER' cannot contain the delimiter character ('$DELIMITER')")

        then(selection.size()).isEqualTo(1)
        then(selection.getWord(0)).isEqualTo("initial")
    }

    @Test
    fun `should convert words to text`() {
        val selection = WordSelection()
        selection.addWord("This")
        selection.addWord("is")
        selection.addWord("a")
        selection.addWord("text.")

        val text = selection.toText()
        then(text).isEqualTo("This is a text.")
    }

    companion object {
        private const val DELIMITER = "|"
    }
}