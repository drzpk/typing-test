package dev.drzepka.typing.server.domain.service

import dev.drzepka.typing.server.AbstractDatabaseTest
import dev.drzepka.typing.server.domain.dto.wordlist.CreateWordListRequest
import dev.drzepka.typing.server.domain.entity.table.WordListsTable
import dev.drzepka.typing.server.domain.exception.ValidationException
import dev.drzepka.typing.server.domain.value.Language
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test

class WordListServiceTest : AbstractDatabaseTest() {
    override val tables = arrayOf(WordListsTable)

    @Test
    fun `should create word list`()  = transaction {
        val request = CreateWordListRequest().apply {
            name = "name"
            language = "pl"
        }

        val created = getService().createWordList(request)

        then(created.name).isEqualTo("name")
        then(created.language).isEqualTo(Language.POLISH)

        Unit
    }

    @Test
    fun `should throw validation error on wrong request`() = transaction {
        val caught = catchThrowable {
            getService().createWordList(CreateWordListRequest())
        }

        then(caught).isInstanceOf(ValidationException::class.java)

        val validationException = caught as ValidationException
        then(validationException.validationErrors.errors).hasSize(2)

        Unit
    }

    private fun getService(): WordListService = WordListService()
}