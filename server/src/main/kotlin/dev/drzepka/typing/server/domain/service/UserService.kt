package dev.drzepka.typing.server.domain.service

import dev.drzepka.typing.server.domain.dto.CreateUserDTO
import dev.drzepka.typing.server.domain.entity.User
import dev.drzepka.typing.server.domain.entity.table.UsersTable
import dev.drzepka.typing.server.domain.util.Logger
import dev.drzepka.typing.server.domain.util.Validators
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class UserService(private val hashService: HashService) {

    private val log by Logger()

    init {
        transaction {
            createAdminUser()
        }
    }

    fun createUser(dto: CreateUserDTO): User {
        transaction {
            val user = User.find { UsersTable.email.lowerCase() eq dto.email!!.toLowerCase() }
            if (user.count() > 0)
                throw IllegalArgumentException("User with email '${dto.email}' already exists")
        }

        if (!Validators.isEmailValid(dto.email!!))
            throw IllegalArgumentException("Email '${dto.email} is invalid")

        // todo: Hiberate validator
        val entity = User.new {
            email = dto.email!!
            displayName = dto.displayName!!
            password = hashService.createHash(dto.password!!)
            createdAt = Instant.now()
        }

        log.info("Created user {} ({})", entity.id, entity.email)
        return entity
    }

    fun findUser(email: String, password: String): User? {
        val user = User.find { UsersTable.email.lowerCase() eq email }.firstOrNull() ?: return null

        if (!hashService.compareHashes(user.password, password)) {
            log.debug("Found user {} but passwords don't match", email)
            return null
        }

        return user
    }

    private fun createAdminUser() {
        val found = transaction {
            val user = User.find { UsersTable.email eq ADMIN_USER_EMAIL }
            user.firstOrNull()
        }
        if (found != null)
            return

        // todo: generate random password
        val randomPassword = "admin"

        log.info("Admin user doesn't exist, creating")
        val dto = CreateUserDTO().apply {
            email = ADMIN_USER_EMAIL
            password = randomPassword
            displayName = "Admin"
        }

        val user = createUser(dto)
        user.activate()
        log.info("Password for admin user '{}' is '{}', be sure to change it!", ADMIN_USER_EMAIL, randomPassword)
    }

    companion object {
        private const val ADMIN_USER_EMAIL = "admin@drzepka.dev"
    }
}