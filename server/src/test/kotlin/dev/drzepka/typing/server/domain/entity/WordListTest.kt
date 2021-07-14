package dev.drzepka.typing.server.domain.entity

import dev.drzepka.typing.server.domain.value.WordListType
import dev.drzepka.typing.server.domain.value.WordSelection
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class WordListTest {

    @Test
    fun `should set random type`() {
        val wordList = WordList()
        wordList.randomTextType()

        then(wordList.type).isEqualTo(WordListType.RANDOM)
        then(wordList.text).isNull()
    }

    @Test
    fun `should set fixed text type`() {
        val text = "some fixed text"
        val selection = WordSelection().apply { loadFromText(text) }

        val wordList = WordList()
        wordList.fixedTextType(selection)

        then(wordList.type).isEqualTo(WordListType.FIXED)
        then(wordList.text).isSameAs(selection)
    }
}