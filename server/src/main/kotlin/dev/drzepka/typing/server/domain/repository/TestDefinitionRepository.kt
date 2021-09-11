package dev.drzepka.typing.server.domain.repository

import dev.drzepka.typing.server.domain.entity.TestDefinition

interface TestDefinitionRepository {
    fun findById(id: Int): TestDefinition?
    fun findByName(name: String): TestDefinition?
    fun findByWordList(wordListId: Int): Collection<TestDefinition>
    fun findAll(active: Boolean? = null): Collection<TestDefinition>
    fun findAllActiveWithCompletedTests(userId: Int): Collection<TestDefinition>
    fun save(definition: TestDefinition)
    fun delete(id: Int): Boolean
}