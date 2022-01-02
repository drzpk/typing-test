package dev.drzepka.typing.server.application.service

import dev.drzepka.typing.server.application.dto.PagedResourceCollection
import dev.drzepka.typing.server.application.dto.user.*
import dev.drzepka.typing.server.application.exception.ErrorCode
import dev.drzepka.typing.server.application.security.PasswordGenerator
import dev.drzepka.typing.server.application.validation.ValidationState
import dev.drzepka.typing.server.application.validation.Validators
import dev.drzepka.typing.server.domain.PagedQuery
import dev.drzepka.typing.server.domain.SearchQuery
import dev.drzepka.typing.server.domain.SortingQuery
import dev.drzepka.typing.server.domain.entity.User
import dev.drzepka.typing.server.domain.entity.User.Companion.ADMIN_USER_EMAIL
import dev.drzepka.typing.server.domain.repository.SessionRepository
import dev.drzepka.typing.server.domain.repository.TestRepository
import dev.drzepka.typing.server.domain.repository.TestResultRepository
import dev.drzepka.typing.server.domain.repository.UserRepository
import dev.drzepka.typing.server.domain.util.Logger
import dev.drzepka.typing.server.domain.value.UserSearchQuery
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class UserService(
    private val hashService: HashService,
    private val userRepository: UserRepository,
    private val testRepository: TestRepository,
    private val testResultRepository: TestResultRepository,
    private val sessionRepository: SessionRepository
) {
    private val log by Logger()

    init {
        transaction {
            createAdminUser()
        }
    }

    fun createUser(request: CreateUserRequest): UserResource {
        return UserResource.fromEntity(doCreateUser(request))
    }

    fun findUser(email: String, password: String): User? {
        val user = userRepository.findByEmail(email) ?: return null

        if (!user.isActive()) {
            log.debug("Found user {} but the account isn't active", email)
            return null
        }

        if (!hashService.compareHashes(user.password, password)) {
            log.debug("Found user {} but passwords don't match", email)
            return null
        }

        return user
    }

    fun searchUsers(request: SearchUsersRequest): PagedResourceCollection<UserResource> {
        val columnMapping = listOf(
            Pair("displayName", "display_name"),
            Pair("createdAt", "created_at"),
            Pair("activatedAt", "activated_at")
        )

        val query = UserSearchQuery()
        query.copyFrom(request as PagedQuery)
        query.copyFrom(request as SortingQuery, columnMapping)
        query.copyFrom(request as SearchQuery)
        query.inactiveOnly = request.inactiveOnly

        val searchResult = userRepository.search(query)
        return PagedResourceCollection.fromPage(searchResult) { UserResource.fromEntity(it) }
    }

    fun deleteUser(userId: Int) {
        log.info("Deleting user {}", userId)
        val user = userRepository.findById(userId) ?: ErrorCode.USER_NOT_FOUND.throwException(userId)

        if (user.isAdmin()) {
            log.warn("An attempt was made to delete admin user {}", userId)
            ErrorCode.CANNOT_DELETE_ADMIN_USER.throwException(userId)
        }

        val deletedResults = testResultRepository.deleteByUserId(userId)
        log.info("Deleted {} test result of user {}", deletedResults, userId)

        val deletedTests = testRepository.deleteByUserId(userId)
        log.info("Deleted {} tests of user {}", deletedTests, userId)

        val deletedSessions = sessionRepository.deleteByUserId(userId)
        log.info("Deleted {} sessions of user {}", deletedSessions, userId)

        userRepository.delete(userId)
    }

    fun activateUser(userId: Int) {
        log.info("Activating user {}", userId)
        val user = userRepository.findById(userId) ?: ErrorCode.USER_NOT_FOUND.throwException(userId)

        if (!user.isActive()) {
            user.activate()
            userRepository.save(user)
        } else {
            log.warn("User {} is already active", userId)
        }
    }

    fun updateSettings(user: User, request: UpdateAccountSettingsRequest) {
        var changed = false

        if (request.displayName != null) {
            if (request.displayName!!.isEmpty() || request.displayName!!.length > 64)
                throw IllegalArgumentException("User's display name must be in range [1,64].")

            log.info(
                "Changing display name of the user {} from '{}' to '{}'",
                user.id, user.displayName, request.displayName
            )

            user.displayName = request.displayName!!
            changed = true
        }

        if (changed)
            userRepository.save(user)
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

        userRepository.save(user)
    }

    private fun oldPasswordMatches(user: User, oldPassword: String): Boolean {
        val current = user.password
        return hashService.compareHashes(current, oldPassword)
    }

    private fun createAdminUser() {
        val found = userRepository.findByEmail(ADMIN_USER_EMAIL)
        if (found != null)
            return

        val randomPassword = PasswordGenerator().generatePassword(10, 16, true)

        log.info("Admin user doesn't exist, creating")
        log.info("Generated random admin password: {}", randomPassword)
        val dto = CreateUserRequest().apply {
            email = ADMIN_USER_EMAIL
            password = randomPassword
            displayName = "Admin"
        }

        val user = doCreateUser(dto)
        user.activate()
        log.info("Password for admin user '{}' is '{}', be sure to change it!", ADMIN_USER_EMAIL, randomPassword)
    }

    private fun doCreateUser(request: CreateUserRequest): User {
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

        if (user.email == ADMIN_USER_EMAIL)
            user.activate()

        userRepository.save(user)
        log.info("Created user {} ({})", user.id, user.email)
        return user
    }
}