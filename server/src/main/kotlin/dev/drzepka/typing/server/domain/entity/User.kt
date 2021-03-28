package dev.drzepka.typing.server.domain.entity

import dev.drzepka.typing.server.domain.entity.table.UsersTable
import dev.drzepka.typing.server.domain.util.Logger
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.Serializable
import java.time.Instant

class User(id: EntityID<Int>) : IntEntity(id), Serializable {
    companion object : IntEntityClass<User>(UsersTable) {
        private val log by Logger()
    }

    var email by UsersTable.email
    var displayName by UsersTable.displayName
    var password by UsersTable.password
    var createdAt: Instant by UsersTable.createdAt
    var activatedAt: Instant? by UsersTable.activatedAt

    fun activate() {
        if (activatedAt != null)
            return

        log.info("Activating user {}", email)
        transaction {
            activatedAt = Instant.now()
        }
    }
}