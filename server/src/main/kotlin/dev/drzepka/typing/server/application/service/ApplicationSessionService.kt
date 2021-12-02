package dev.drzepka.typing.server.application.service

import dev.drzepka.typing.server.application.TypingTestSession
import dev.drzepka.typing.server.domain.entity.User
import dev.drzepka.typing.server.domain.util.Logger
import io.ktor.application.*

class ApplicationSessionService(private val userSessionService: UserSessionService) {

    private val log by Logger()

    fun createApplicationSession(user: User?, call: ApplicationCall): TypingTestSession {
        val userSession = userSessionService.createUserSession(user, call)
        val appSession = TypingTestSession(userSession.id!!, user?.id, user?.isAdmin() == true)

        log.info("Created application session {}", appSession)
        return appSession
    }
}