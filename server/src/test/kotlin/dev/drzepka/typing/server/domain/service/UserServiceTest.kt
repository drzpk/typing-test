package dev.drzepka.typing.server.domain.service

import dev.drzepka.typing.server.AbstractDatabaseTest
import dev.drzepka.typing.server.application.dto.user.CreateUserRequest
import dev.drzepka.typing.server.application.dto.user.SearchUsersRequest
import dev.drzepka.typing.server.application.dto.user.UpdateAccountSettingsRequest
import dev.drzepka.typing.server.application.exception.ErrorCode
import dev.drzepka.typing.server.application.exception.ErrorCodeException
import dev.drzepka.typing.server.application.service.UserService
import dev.drzepka.typing.server.domain.Page
import dev.drzepka.typing.server.domain.SortingQuery
import dev.drzepka.typing.server.domain.entity.User
import dev.drzepka.typing.server.domain.repository.TestRepository
import dev.drzepka.typing.server.domain.repository.TestResultRepository
import dev.drzepka.typing.server.domain.repository.UserRepository
import dev.drzepka.typing.server.domain.value.UserSearchQuery
import dev.drzepka.typing.server.infrastructure.PBKDF2HashService
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.time.Instant

class UserServiceTest : AbstractDatabaseTest() {

    private lateinit var userRepository: UserRepository
    private lateinit var testRepository: TestRepository
    private lateinit var testResultRepository: TestResultRepository

    @BeforeEach
    fun prepare() {
        userRepository = mock()
        testRepository = mock()
        testResultRepository = mock()
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

        whenever(userRepository.save(any())).thenAnswer {
            val user = it.getArgument(0, User::class.java)
            user.id = 123
            user
        }

        val user = service.createUser(dto)

        val captor = argumentCaptor<User>()
        verify(userRepository, times(1)).save(captor.capture())

        then(user.email).isEqualTo(dto.email)
        then(user.displayName).isEqualTo(dto.displayName)
    }

    @Test
    fun `should find existing user`() {
        val hashService = PBKDF2HashService()
        val existingUser = User().apply {
            email = "email@email.com"
            password = hashService.createHash("password")
            displayName = "display"
            createdAt = Instant.now()
            activatedAt = Instant.now()
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

    @Test
    fun `should search users`() {
        val request = SearchUsersRequest().apply {
            page = 2
            size = 8
            properties = listOf(SortingQuery.Property("email", SortingQuery.Order.DESC))
            phrase = "search"
        }

        whenever(userRepository.search(any())).thenReturn(Page.empty())

        getService().searchUsers(request)

        val captor = argumentCaptor<UserSearchQuery>()
        verify(userRepository).search(captor.capture())

        val query = captor.firstValue
        then(query.page).isEqualTo(request.page)
        then(query.size).isEqualTo(request.size)
        then(query.properties).isEqualTo(request.properties)
        then(query.phrase).isEqualTo(request.phrase)
    }

    @Test
    fun `should delete user`() {
        val user = User().apply {
            id = 123
        }
        whenever(userRepository.findById(eq(123))).thenReturn(user)

        getService().deleteUser(123)

        verify(testResultRepository).deleteByUserId(eq(123))
        verify(testRepository).deleteByUserId(eq(123))
        verify(userRepository).delete(eq(123))
    }

    @Test
    fun `should not delete non-existent user`() {
        val throwable = catchThrowable { getService().deleteUser(99) }

        then(throwable).isInstanceOf(ErrorCodeException::class.java)

        val errorCodeException = throwable as ErrorCodeException
        then(errorCodeException.code).isEqualTo(ErrorCode.USER_NOT_FOUND)
        then(errorCodeException.`object`).isEqualTo(99)
    }

    @Test
    fun `should activate user`() {
        val user = User().apply { id=112 }
        whenever(userRepository.findById(eq(user.id!!))).thenReturn(user)

        getService().activateUser(112)

        then(user.isActive()).isTrue()
        verify(userRepository).save(same(user))
    }

    @Test
    fun `should update settings`() {
        val user = User()
        val request = UpdateAccountSettingsRequest().apply {
            displayName = "new display name"
        }

        getService().updateSettings(user, request)

        then(user.displayName).isEqualTo(request.displayName)
        verify(userRepository, times(1)).save(same(user))
    }

    private fun getService(): UserService {
        return UserService(PBKDF2HashService(), userRepository, testRepository, testResultRepository)
    }
}