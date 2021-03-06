package dev.drzepka.typing.server.infrastructure.repository

import dev.drzepka.typing.server.domain.Page
import dev.drzepka.typing.server.domain.PagedQuery
import dev.drzepka.typing.server.domain.entity.Test
import dev.drzepka.typing.server.domain.repository.TestDefinitionRepository
import dev.drzepka.typing.server.domain.repository.TestRepository
import dev.drzepka.typing.server.domain.repository.UserRepository
import dev.drzepka.typing.server.domain.value.UserIdentity
import dev.drzepka.typing.server.domain.value.WordSelection
import dev.drzepka.typing.server.infrastructure.exception.DataIntegrityException
import dev.drzepka.typing.server.infrastructure.repository.table.Tests
import dev.drzepka.typing.server.infrastructure.util.NullableForeignKeyWrapper
import dev.drzepka.typing.server.infrastructure.util.countAllRows
import dev.drzepka.typing.server.infrastructure.util.countWhere
import dev.drzepka.typing.server.infrastructure.util.paged
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import java.nio.charset.StandardCharsets
import java.time.Duration

class ExposedTestRepository(
    private val testDefinitionRepository: TestDefinitionRepository,
    private val userRepository: UserRepository
) : TestRepository {

    override fun save(test: Test) {
        if (test.takenBy.sessionId == null) {
            // SessionId is nullable only for backward compatibility and mustn't be used in new records
            throw DataIntegrityException("Cannot save test with null sessionId")
        }

        if (test.isStored()) {
            Tests.update({ Tests.id eq test.id }) {
                testToRow(test, it)
            }
        } else {
            val id = Tests.insertAndGetId {
                testToRow(test, it)
            }

            test.id = id.value
        }
    }

    override fun findById(id: Int): Test? {
        return Tests.select { Tests.id eq id }
            .singleOrNull()
            ?.let { rowToTest(it) }
    }

    override fun findPagedByUserId(userId: Int, pagination: PagedQuery): Page<Test> {
        val totalElements = Tests.countAllRows()
        val list = Tests.select { Tests.user eq userId }
            .paged(pagination)
            .map { rowToTest(it) }

        return Page(list, pagination, totalElements)
    }

    override fun findNotStartedBySessionIdAndTestDefinitionId(sessionId: Int, testDefinitionId: Int): Collection<Test> {
        return Tests.select { (Tests.session eq sessionId) and (Tests.testDefinition eq testDefinitionId) and Tests.startedAt.isNull() }
            .map { rowToTest(it) }
    }

    override fun delete(id: Int): Boolean {
        val count = Tests.deleteWhere { Tests.id eq id }
        return count > 0
    }

    override fun deleteByUserId(userId: Int): Int {
        return Tests.deleteWhere { Tests.user eq userId }
    }

    override fun assignAnonymousTestsToUser(sessionId: Int, userId: Int): List<Int> {
        val testIds = Tests.slice(Tests.id)
            .select { Tests.user.isNull() and (Tests.session eq sessionId) }
            .map { it[Tests.id].value }

        Tests.update({ Tests.id.inList(testIds) }) {
            it[user] = NullableForeignKeyWrapper(userId)
        }

        return testIds
    }

    override fun countByTestDefinitionId(testDefinitionId: Int): Int {
        return Tests.countWhere { Tests.testDefinition eq testDefinitionId }.toInt()
    }

    private fun testToRow(test: Test, stmt: UpdateBuilder<Int>) {
        stmt[Tests.testDefinition] = test.testDefinition.id!!
        stmt[Tests.user] = NullableForeignKeyWrapper(test.takenBy.user?.id)
        stmt[Tests.session] = NullableForeignKeyWrapper(test.takenBy.sessionId)
        stmt[Tests.state] = test.state

        stmt[Tests.createdAt] = test.createdAt
        stmt[Tests.startedAt] = test.startedAt
        stmt[Tests.finishedAt] = test.finishedAt

        stmt[Tests.startTimeLimit] = test.startTimeLimit?.seconds?.toInt()
        stmt[Tests.finishTimeLimit] = test.finishTimeLimit?.seconds?.toInt()

        stmt[Tests.selectedWords] = test.selectedWords.serialize().toByteArray().let { ExposedBlob(it) }
        stmt[Tests.enteredWords] = test.enteredWords?.serialize()?.toByteArray()?.let { ExposedBlob(it) }

        stmt[Tests.backspaceCount] = test.backspaceCount
        stmt[Tests.wordRegenerationCount] = test.wordRegenerationCount
    }

    private fun rowToTest(row: ResultRow): Test {
        val definition = testDefinitionRepository.findById(row[Tests.testDefinition].value)!!
        val user = row[Tests.user]?.value?.let { userRepository.findById(it) }
        val selectedWords = row[Tests.selectedWords].bytes.let {
            val selection = WordSelection()
            selection.deserialize(String(it, StandardCharsets.UTF_8))
            selection
        }

        val identity = UserIdentity(user, row[Tests.session]?.value ?: 0)
        return Test(definition, identity, selectedWords).apply {
            id = row[Tests.id].value

            createdAt = row[Tests.createdAt]
            startedAt = row[Tests.startedAt]
            finishedAt = row[Tests.finishedAt]

            startTimeLimit = Duration.ofSeconds(row[Tests.startTimeLimit]!!.toLong())
            finishTimeLimit = row[Tests.finishTimeLimit]?.let { Duration.ofSeconds(it.toLong()) }

            enteredWords =
                row[Tests.enteredWords]?.let { blob -> WordSelection().apply { deserialize(blob.bytes.decodeToString()) } }
            wordRegenerationCount = row[Tests.wordRegenerationCount]
        }
    }
}