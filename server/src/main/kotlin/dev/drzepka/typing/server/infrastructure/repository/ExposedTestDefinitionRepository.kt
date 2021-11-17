package dev.drzepka.typing.server.infrastructure.repository

import dev.drzepka.typing.server.domain.entity.TestDefinition
import dev.drzepka.typing.server.domain.repository.TestDefinitionRepository
import dev.drzepka.typing.server.domain.repository.WordListRepository
import dev.drzepka.typing.server.infrastructure.repository.table.TestDefinitions
import dev.drzepka.typing.server.infrastructure.repository.table.Tests
import org.jetbrains.exposed.sql.*
import java.time.Duration

class ExposedTestDefinitionRepository(private val wordListRepository: WordListRepository) : TestDefinitionRepository {

    override fun findById(id: Int): TestDefinition? {
        return TestDefinitions.select { TestDefinitions.id eq id }
            .singleOrNull()
            ?.let { rowToTestDefinition(it) }
    }

    override fun findByName(name: String): TestDefinition? {
        return TestDefinitions.select { TestDefinitions.name eq name }
            .singleOrNull()
            ?.let { rowToTestDefinition(it) }
    }

    override fun findByWordList(wordListId: Int): Collection<TestDefinition> {
        return TestDefinitions.select { TestDefinitions.wordList eq wordListId }
            .map { rowToTestDefinition(it) }
    }

    override fun findAll(active: Boolean?): Collection<TestDefinition> {
        val query = if (active != null)
            TestDefinitions.select { TestDefinitions.isActive eq active }
        else
            TestDefinitions.selectAll()

        return query.map { rowToTestDefinition(it) }
    }

    override fun findAllActiveWithCompletedTests(userId: Int): Collection<TestDefinition> {
        val ids = (TestDefinitions innerJoin Tests)
            .slice(TestDefinitions.id)
            .select { Tests.finishedAt.isNotNull() and (TestDefinitions.isActive eq true) }
            .groupBy(TestDefinitions.id)
            .map { it[TestDefinitions.id].value }

        return TestDefinitions.select { TestDefinitions.id inList ids }
            .map { rowToTestDefinition(it) }
    }

    @Suppress("DuplicatedCode")
    override fun save(definition: TestDefinition) {
        if (definition.isStored()) {
            TestDefinitions.update({ TestDefinitions.id eq definition.id }) {
                it[name] = definition.name
                it[wordList] = definition.wordList.id!!
                it[duration] = definition.duration?.seconds?.toInt()
                it[isActive] = definition.isActive
                it[createdAt] = definition.createdAt
                it[modifiedAt] = definition.modifiedAt
            }
        } else {
            val id = TestDefinitions.insertAndGetId {
                it[name] = definition.name
                it[wordList] = definition.wordList.id!!
                it[duration] = definition.duration?.seconds?.toInt()
                it[isActive] = definition.isActive
                it[createdAt] = definition.createdAt
                it[modifiedAt] = definition.modifiedAt
            }

            definition.id = id.value
        }
    }

    override fun delete(id: Int): Boolean {
        val count = TestDefinitions.deleteWhere { TestDefinitions.id eq id }
        return count > 0
    }

    private fun rowToTestDefinition(row: ResultRow): TestDefinition {
        return TestDefinition().apply {
            id = row[TestDefinitions.id].value
            name = row[TestDefinitions.name]
            wordList = wordListRepository.findById(row[TestDefinitions.wordList].value)!!
            duration = row[TestDefinitions.duration]?.toLong()?.let { Duration.ofSeconds(it) }
            isActive = row[TestDefinitions.isActive]
            createdAt = row[TestDefinitions.createdAt]
            modifiedAt = row[TestDefinitions.modifiedAt]
        }
    }
}