package dev.drzepka.typing.server.application.util

import dev.drzepka.typing.server.application.TypingTestSession
import dev.drzepka.typing.server.application.exception.SecurityException
import dev.drzepka.typing.server.domain.entity.User
import dev.drzepka.typing.server.domain.repository.UserRepository
import dev.drzepka.typing.server.domain.value.UserIdentity
import io.ktor.application.*
import io.ktor.sessions.*
import io.ktor.util.pipeline.*

fun PipelineContext<*, ApplicationCall>.getCurrentIdentity(userRepository: UserRepository): UserIdentity {
    val session = call.sessions.get<TypingTestSession>()!!
    val user = session.userId?.let { userRepository.findById(it) }
    return UserIdentity(user, session.userSessionId)
}

fun PipelineContext<*, ApplicationCall>.getCurrentUser(userRepository: UserRepository): User? {
    val session = call.sessions.get<TypingTestSession>()!!
    return session.userId?.let { userRepository.findById(it) }
}

fun PipelineContext<*, ApplicationCall>.getCurrentRegisteredUser(userRepository: UserRepository): User {
    return getCurrentUser(userRepository)
        ?: throw SecurityException("Anonymous user doesn't have permissions to access this resource.")
}

fun PipelineContext<*, ApplicationCall>.clearUserSession() {
    call.sessions.clear<TypingTestSession>()
}