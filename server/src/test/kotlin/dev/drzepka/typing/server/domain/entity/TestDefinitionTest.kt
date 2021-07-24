package dev.drzepka.typing.server.domain.entity

import dev.drzepka.typing.server.domain.value.WordSelection
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

internal class TestDefinitionTest {

    @Test
    fun `should return fixed text if word list type is FIXED`() {
        val selection = WordSelection().apply { loadFromText("example text") }

        val wordList = WordList()
        wordList.fixedTextType(selection)

        val definition = TestDefinition()
        definition.wordList = wordList

        val returnedSelection = definition.getFixedText()
        then(returnedSelection).isSameAs(selection)
    }

    @Test
    fun `should not return fixed text when word list type is RANDOM`() {
        val wordList = WordList()
        wordList.randomTextType()

        val definition = TestDefinition()
        definition.wordList = wordList

        val returnedSelection = definition.getFixedText()
        then(returnedSelection).isNull()
    }
}