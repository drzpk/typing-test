package dev.drzepka.typing.server.application.service

import dev.drzepka.typing.server.application.TypingTestSession
import dev.drzepka.typing.server.domain.entity.User
import dev.drzepka.typing.server.domain.repository.SessionRepository
import dev.drzepka.typing.server.infrastructure.service.TransactionService
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import org.assertj.core.api.BDDAssertions.then
import org.assertj.core.data.TemporalUnitWithinOffset
import org.jetbrains.exposed.sql.Transaction
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.time.Instant
import java.time.temporal.ChronoUnit

@ExperimentalCoroutinesApi
@ExtendWith(MockitoExtension::class)
internal class UserSessionServiceTest {

    private val context = newSingleThreadContext("name")
    private val sessionRepository = mock<SessionRepository>()
    private val transactionService = mock<TransactionService>()

    @Test
    fun `should create user session`() = withTestApplication {
        val user = User().apply {
            id = 2134
        }

        val call = TestApplicationCall(application, coroutineContext = context)
        val userAgent = "user agent"
        call.request.addHeader(HttpHeaders.UserAgent, userAgent)

        val session = getService().createUserSession(user, call)

        then(session.ipAddress).isEqualTo("localhost") // defined in TestApplicationRequest
        then(session.userAgent).isEqualTo(userAgent)
        then(session.createdAt).isCloseTo(Instant.now(), TemporalUnitWithinOffset(500, ChronoUnit.MILLIS))
        then(session.createdAt).isEqualTo(session.lastSeen)
        verify(sessionRepository).save(same(session))
    }

    @Test
    fun `should create anonymous user session`() = withTestApplication {
        val call = TestApplicationCall(application, coroutineContext = context)

        val session = getService().createUserSession(null, call)

        then(session.userId).isNull()
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `should update last seen`() {
        val sessionId = 1382
        val appSession = TypingTestSession(sessionId, 123, false)

        whenever(transactionService.transaction(any<Transaction.() -> Any>())).thenAnswer {
            val callable = it.arguments.first() as (Transaction.() -> Any)
            callable.invoke(mock())
        }

        getService().updateLastSeen(appSession)

        val captor = argumentCaptor<Instant>()
        verify(sessionRepository).updateLastSeen(eq(sessionId), captor.capture())
        verify(transactionService).transaction(any<Transaction.() -> Any>())

        then(captor.firstValue).isCloseTo(Instant.now(), TemporalUnitWithinOffset(500, ChronoUnit.MILLIS))
    }

    private fun getService(): UserSessionService = UserSessionService(sessionRepository, transactionService)
}