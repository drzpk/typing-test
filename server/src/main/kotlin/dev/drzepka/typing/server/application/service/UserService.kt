package dev.drzepka.typing.server.application.service

import dev.drzepka.typing.server.application.dto.user.ChangePasswordRequest
import dev.drzepka.typing.server.application.dto.user.CreateUserRequest
import dev.drzepka.typing.server.application.dto.user.UpdateAccountSettingsRequest
import dev.drzepka.typing.server.application.validation.ValidationState
import dev.drzepka.typing.server.application.validation.Validators
import dev.drzepka.typing.server.domain.entity.User
import dev.drzepka.typing.server.domain.entity.User.Companion.ADMIN_USER_EMAIL
import dev.drzepka.typing.server.domain.repository.UserRepository
import dev.drzepka.typing.server.domain.util.Logger
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class UserService(private val hashService: HashService, private val userRepository: UserRepository) {
    private val log by Logger()

    init {
        transaction {
            createAdminUser()
        }
    }

    fun createUser(request: CreateUserRequest): User {
        val validation = ValidationState()

        val existingUser = userRepository.findByEmail(request.email!!.toLowerCase())
        if (existingUser != null)
            validation.addFieldError("email", "User with email '${request.email}' already exists.")

        if (!Validators.isEmailValid(request.email!!))
            validation.addFieldError("email", "Email '${request.email} is invalid")

        validation.verify()

        val user = User()
        user.email = request.email!!
        user.displayName = request.displayName!!
        user.password = hashService.createHash(request.password!!)
        user.createdAt = Instant.now()

        userRepository.save(user)
        log.info("Created user {} ({})", user.id, user.email)
        return user
    }

    fun findUser(email: String, password: String): User? {
        val user = userRepository.findByEmail(email) ?: return null

        if (!hashService.compareHashes(user.password, password)) {
            log.debug("Found user {} but passwords don't match", email)
            return null
        }

        return user
    }

    fun updateSettings(user: User, request: UpdateAccountSettingsRequest) {
        if (request.displayName.isEmpty() || request.displayName.length > 64)
            throw IllegalArgumentException("User's display name must be in range [1,64].")

        transaction {
            log.info(
                "Changing display name of the user {} from '{}' to '{}'",
                user.id, user.displayName, request.displayName
            )
            user.displayName = request.displayName
        }
    }

    fun changePassword(user: User, request: ChangePasswordRequest) {
        val errors = ValidationState()
        if (!oldPasswordMatches(user, request.oldPassword))
            errors.addFieldError("oldPassword", "Incorrect password.")
        if (request.oldPassword == request.newPassword)
            errors.addFieldError("newPassword", "New password cannot be the same as the old one.")

        errors.verify()

        log.info("Changing password of the user {}", user.id)
        val newHash = hashService.createHash(request.newPassword)
        user.password = newHash
    }

    private fun oldPasswordMatches(user: User, oldPassword: String): Boolean {
        val current = user.password
        return hashService.compareHashes(current, oldPassword)
    }

    private fun createAdminUser() {
        val found = userRepository.findByEmail(ADMIN_USER_EMAIL)
        if (found != null)
            return

        // todo: generate random password
        val randomPassword = "admin"

        log.info("Admin user doesn't exist, creating")
        val dto = CreateUserRequest().apply {
            email = ADMIN_USER_EMAIL
            password = randomPassword
            displayName = "Admin"
        }

        val user = createUser(dto)
        user.activate()
        log.info("Password for admin user '{}' is '{}', be sure to change it!", ADMIN_USER_EMAIL, randomPassword)
    }
}