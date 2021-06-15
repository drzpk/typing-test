package dev.drzepka.typing.server.infrastructure.repository

import dev.drzepka.typing.server.domain.Page
import dev.drzepka.typing.server.domain.entity.User
import dev.drzepka.typing.server.domain.repository.UserRepository
import dev.drzepka.typing.server.domain.value.UserSearchQuery
import dev.drzepka.typing.server.infrastructure.repository.table.Users
import dev.drzepka.typing.server.infrastructure.util.filtered
import dev.drzepka.typing.server.infrastructure.util.paged
import dev.drzepka.typing.server.infrastructure.util.sorted
import org.jetbrains.exposed.sql.*

class ExposedUserRepository : UserRepository {

    override fun findById(id: Int): User? {
        return Users.select {Users.id eq id }
            .singleOrNull()
            ?.let { rowToUser(it) }
    }

    override fun findByEmail(email: String): User? {
        return Users.select { Users.email eq email }
            .singleOrNull()
            ?.let { rowToUser(it) }
    }

    override fun search(query: UserSearchQuery): Page<User> {
        val dbQuery = Users.selectAll()
            .sorted(query)
            .filtered(Users.email, query)

        if (query.inactiveOnly)
            dbQuery.andWhere { Users.activatedAt.isNull() }

        val countExpression = Users.id.count()
        val total = dbQuery.copy()
            .adjustSlice { slice(countExpression) }
            .first()[countExpression]

        val result = dbQuery
            .paged(query)
            .map { rowToUser(it) }

        return Page(result, query, total)
    }

    override fun save(user: User) {
        if (user.isStored()) {
            Users.update({ Users.id eq user.id }) {
                it[email] = user.email
                it[displayName] = user.displayName
                it[password] = user.password
                it[createdAt] = user.createdAt
                it[activatedAt] = user.activatedAt
            }
        } else {
            val id = Users.insertAndGetId {
                it[email] = user.email
                it[displayName] = user.displayName
                it[password] = user.password
                it[createdAt] = user.createdAt
                it[activatedAt] = user.activatedAt
            }

            user.id = id.value
        }
    }

    override fun delete(userId: Int): Boolean {
        val count = Users.deleteWhere { Users.id eq userId }
        return count > 0
    }

    private fun rowToUser(row: ResultRow): User {
        return User().apply {
            id = row[Users.id].value
            email = row[Users.email]
            displayName = row[Users.displayName]
            password = row[Users.password]
            createdAt = row[Users.createdAt]
            activatedAt = row[Users.activatedAt]
        }
    }
}