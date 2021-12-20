package dev.drzepka.typing.server.domain.repository

import dev.drzepka.typing.server.domain.Page
import dev.drzepka.typing.server.domain.PagedQuery
import dev.drzepka.typing.server.domain.entity.Test

interface TestRepository {
    fun save(test: Test)
    fun findById(id: Int): Test?
    fun findPagedByUserId(userId: Int, pagination: PagedQuery): Page<Test>
    fun findNotStartedBySessionIdAndTestDefinitionId(sessionId: Int, testDefinitionId: Int): Collection<Test>
    fun delete(id: Int): Boolean
    fun deleteByUserId(userId: Int): Int
    fun assignAnonymousTestsToUser(sessionId: Int, userId: Int): List<Int>
    fun countByTestDefinitionId(testDefinitionId: Int): Int
}