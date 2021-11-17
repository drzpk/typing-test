package dev.drzepka.typing.server.domain.entity

import dev.drzepka.typing.server.domain.value.Language
import dev.drzepka.typing.server.domain.value.WordListType
import dev.drzepka.typing.server.domain.value.WordSelection

class WordList : AbstractEntity<Int>() {
    var name = ""
    var language = Language.ENGLISH
    var type = WordListType.RANDOM
        private set

    /**
     * Non-null value when [type] is [WordListType.FIXED]. Stored in
     */
    var text: WordSelection? = null
        private set

    fun randomTextType() {
        type = WordListType.RANDOM
        text = null
    }

    fun fixedTextType(text: String) {
        val selection = WordSelection()
        selection.loadFromText(text)
        fixedTextType(selection)
    }

    fun fixedTextType(text: WordSelection) {
        type = WordListType.FIXED
        this.text = text
    }
}