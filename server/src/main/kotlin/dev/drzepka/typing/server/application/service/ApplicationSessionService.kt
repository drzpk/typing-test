package dev.drzepka.typing.server.application.service

import dev.drzepka.typing.server.application.TypingTestSession
import dev.drzepka.typing.server.domain.entity.User
import io.ktor.application.*

class ApplicationSessionService(private val userSessionService: UserSessionService) {

    fun createApplicationSession(user: User, call: ApplicationCall): TypingTestSession {
        val userSession = userSessionService.createUserSession(user, call)
        return TypingTestSession(userSession.id!!, user.id!!, user.isAdmin())
    }
}