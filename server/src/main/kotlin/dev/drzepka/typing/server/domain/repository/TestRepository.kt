package dev.drzepka.typing.server.domain.repository

import dev.drzepka.typing.server.domain.Page
import dev.drzepka.typing.server.domain.PagedQuery
import dev.drzepka.typing.server.domain.entity.Test

interface TestRepository {
    fun save(test: Test)
    fun findById(id: Int): Test?
    fun findPagedByUserId(userId: Int, pagination: PagedQuery): Page<Test>
    fun findNotStartedByUserIdAndTestDefinitionId(userId: Int, testDefinitionId: Int): Collection<Test>
    fun delete(id: Int): Boolean
}