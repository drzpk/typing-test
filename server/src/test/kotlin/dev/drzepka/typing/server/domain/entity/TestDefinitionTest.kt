package dev.drzepka.typing.server.domain.entity

import dev.drzepka.typing.server.domain.exception.DomainValidationException
import dev.drzepka.typing.server.domain.value.WordSelection
import org.assertj.core.api.BDDAssertions.*
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

    @Test
    fun `should only allow null duration with fixed test-typed word list`() {
        val testDefinition = TestDefinition()
        testDefinition.wordList = WordList().apply { fixedTextType("test") }

        assertThatCode {
            testDefinition.duration = null
        }.doesNotThrowAnyException()

        testDefinition.wordList.randomTextType()
        assertThatExceptionOfType(DomainValidationException::class.java).isThrownBy {
            testDefinition.duration = null
        }.withMessage("Null duration may only be used with fixed-type word lists.")
    }
}