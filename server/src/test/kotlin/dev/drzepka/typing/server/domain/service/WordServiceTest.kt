package dev.drzepka.typing.server.domain.service

import dev.drzepka.typing.server.AbstractDatabaseTest
import dev.drzepka.typing.server.domain.dto.word.AddWordRequest
import dev.drzepka.typing.server.domain.entity.WordList
import dev.drzepka.typing.server.domain.entity.table.WordListsTable
import dev.drzepka.typing.server.domain.entity.table.WordsTable
import dev.drzepka.typing.server.domain.exception.ValidationException
import dev.drzepka.typing.server.domain.value.Language
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test

class WordServiceTest : AbstractDatabaseTest() {
    override val tables = arrayOf(WordListsTable, WordsTable)

    @Test
    fun `should add word`() = transaction {
        val wordList = WordList.new {
            name = "test"
            language = Language.ENGLISH
        }

        val request = AddWordRequest().apply {
            word = "abc"
            popularity = 1
            wordListId = wordList.id.value
        }

        val added = getService().addWord(request)

        then(added.word).isEqualTo("abc")
        then(added.popularity).isEqualTo(1)
        then(added.wordList.id.value).isEqualTo(wordList.id.value)

        Unit
    }

    @Test
    fun `should throw validation error on wrong word data`() = transaction {
        val caught = catchThrowable {
            getService().addWord(AddWordRequest())
        }

        then(caught).isInstanceOf(ValidationException::class.java)
        then((caught as ValidationException).validationErrors.errors).isNotEmpty

        Unit
    }

    private fun getService(): WordService = WordService()
}