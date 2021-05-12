package dev.drzepka.typing.server.domain.service

import dev.drzepka.typing.server.AbstractDatabaseTest
import dev.drzepka.typing.server.application.dto.user.CreateUserRequest
import dev.drzepka.typing.server.application.service.UserService
import dev.drzepka.typing.server.domain.entity.User
import dev.drzepka.typing.server.domain.repository.UserRepository
import dev.drzepka.typing.server.infrastructure.PBKDF2HashService
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.time.Instant

class UserServiceTest : AbstractDatabaseTest() {

    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun prepare() {
        userRepository = mock()
    }

    @Test
    fun `should create user`() {
        val dto = CreateUserRequest().apply {
            email = "email@email.com"
            displayName = "display name"
            password = "Password123"
        }

        val service = getService()
        reset(userRepository) // Reset count (admin user is created)
        val user = service.createUser(dto)

        val captor = argumentCaptor<User>()
        verify(userRepository, times(1)).save(captor.capture())
        then(captor.firstValue).isSameAs(user)

        then(user.email).isEqualTo(dto.email)
        then(user.displayName).isEqualTo(dto.displayName)
        then(user.password).isNotBlank()
    }

    @Test
    fun `should find existing user`() {
        val hashService = PBKDF2HashService()
        val existingUser = User().apply {
            email = "email@email.com"
            password = hashService.createHash("password")
            displayName = "display"
            createdAt = Instant.now()
        }

        whenever(userRepository.findByEmail("email@email.com")).thenReturn(existingUser)

        val found = getService().findUser("email@email.com", "password")
        then(found).isSameAs(existingUser)
    }

    @Test
    fun `should not find non-existent user`() {
        val user = getService().findUser("email@email.com", "password")
        then(user).isNull()
    }

    @Test
    fun `should not find user with wrong password`() {
        val hashService = PBKDF2HashService()
        val existingUser = User().apply {
            email = "email@email.com"
            password = hashService.createHash("password1")
            displayName = "display"
            createdAt = Instant.now()
        }

        whenever(userRepository.findByEmail("email@email.com")).thenReturn(existingUser)

        val user = getService().findUser("email@email.com", "password")
        then(user).isNull()
    }

    private fun getService(): UserService {
        return UserService(PBKDF2HashService(), userRepository)
    }
}