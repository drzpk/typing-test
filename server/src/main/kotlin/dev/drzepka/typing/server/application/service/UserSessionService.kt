package dev.drzepka.typing.server.application.service

import dev.drzepka.typing.server.application.TypingTestSession
import dev.drzepka.typing.server.application.util.HttpUtils
import dev.drzepka.typing.server.domain.entity.Session
import dev.drzepka.typing.server.domain.entity.User
import dev.drzepka.typing.server.domain.repository.SessionRepository
import dev.drzepka.typing.server.domain.util.Logger
import dev.drzepka.typing.server.infrastructure.service.TransactionService
import io.ktor.application.*
import io.ktor.sessions.*
import java.time.Instant

class UserSessionService(private val sessionRepository: SessionRepository, private val transactionService: TransactionService) {

    private val log by Logger()

    fun createUserSession(user: User, call: ApplicationCall): Session {
        val session = Session(user.id!!).apply {
            ipAddress = HttpUtils.getRemoteIp(call)
            userAgent = HttpUtils.getUserAgent(call)
        }

        sessionRepository.save(session)
        log.info("Created new session {}", session.id)
        return session
    }

    fun updateLastSeen(call: ApplicationCall) {
        val appSession = call.sessions.get<TypingTestSession>()!!
        updateLastSeen(appSession)
    }

    fun updateLastSeen(appSession: TypingTestSession) {
        val lastSeen = Instant.now()
        log.debug("Updating last seen for session {} (user {}) to {}", appSession.sessionId, appSession.userId, lastSeen)

        // Update the last seen marker even if there's an error somewhere else that would cause a rollback
        transactionService.transaction {
            sessionRepository.updateLastSeen(appSession.sessionId, lastSeen)
        }
    }
}