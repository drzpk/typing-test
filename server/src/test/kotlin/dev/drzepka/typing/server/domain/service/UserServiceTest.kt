package dev.drzepka.typing.server.domain.service

import dev.drzepka.typing.server.AbstractDatabaseTest
import dev.drzepka.typing.server.domain.dto.CreateUserRequest
import dev.drzepka.typing.server.domain.entity.User
import dev.drzepka.typing.server.domain.entity.table.UsersTable
import dev.drzepka.typing.server.infrastructure.PBKDF2HashService
import org.assertj.core.api.BDDAssertions.then
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test
import java.time.Instant

class UserServiceTest : AbstractDatabaseTest() {

    override val tables = arrayOf(UsersTable)

    @Test
    fun `should create user`() = transaction {
        val dto = CreateUserRequest().apply {
            email = "email@email.com"
            displayName = "display name"
            password = "Password123"
        }

        val user = getService().createUser(dto)

        then(user.email).isEqualTo(dto.email)
        then(user.displayName).isEqualTo(dto.displayName)
        then(user.password).isNotBlank()
        then(User.findById(user.id)).isNotNull

        Unit
    }

    @Test
    fun `should find existing user`() = transaction {
        val hashService = PBKDF2HashService()
        User.new {
            email = "email@email.com"
            password = hashService.createHash("password")
            displayName = "display"
            createdAt = Instant.now()
        }

        val user = getService().findUser("email@email.com", "password")
        then(user).isNotNull

        Unit
    }

    @Test
    fun `should not find non-existent user`() = transaction {
        val user = getService().findUser("email@email.com", "password")
        then(user).isNull()
    }

    @Test
    fun `should not find user with wrong password`() = transaction {
        val hashService = PBKDF2HashService()
        User.new {
            email = "email@email.com"
            password = hashService.createHash("password1")
            displayName = "display"
            createdAt = Instant.now()
        }

        val user = getService().findUser("email@email.com", "password")
        then(user).isNull()
    }

    private fun getService(): UserService {
        return UserService(PBKDF2HashService())
    }
}