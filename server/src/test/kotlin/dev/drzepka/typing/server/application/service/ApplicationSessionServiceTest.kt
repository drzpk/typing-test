package dev.drzepka.typing.server.application.service

import dev.drzepka.typing.server.domain.entity.Session
import dev.drzepka.typing.server.domain.entity.User
import io.ktor.application.*
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.isNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.same
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
internal class ApplicationSessionServiceTest {

    private val userSessionService = mock<UserSessionService>()

    @Test
    fun `should create registered user session`() {
        val user = User().apply { id = 11; email = "admin@drzepka.dev" }
        val call = mock<ApplicationCall>()

        val userSession = Session(123)
        userSession.id = 991
        whenever(userSessionService.createUserSession(same(user), same(call))).thenReturn(userSession)

        val session = getService().createApplicationSession(user, call)

        then(session.userSessionId).isEqualTo(userSession.id)
        then(session.userId).isEqualTo(user.id)
        then(session.isAdmin).isTrue
    }

    @Test
    fun `should create anonymous user session`() {
        val call = mock<ApplicationCall>()

        val userSession = Session(123)
        userSession.id = 991
        whenever(userSessionService.createUserSession(isNull(), same(call))).thenReturn(userSession)

        val session = getService().createApplicationSession(null, call)

        then(session.userSessionId).isEqualTo(userSession.id)
        then(session.userId).isNull()
        then(session.isAdmin).isFalse
    }

    private fun getService(): ApplicationSessionService = ApplicationSessionService(userSessionService)
}