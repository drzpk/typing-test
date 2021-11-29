package dev.drzepka.typing.server.domain.util

import dev.drzepka.typing.server.application.TypingTestSession
import dev.drzepka.typing.server.domain.entity.User
import dev.drzepka.typing.server.domain.repository.UserRepository
import dev.drzepka.typing.server.domain.value.UserIdentity
import io.ktor.application.*
import io.ktor.sessions.*
import io.ktor.util.pipeline.*

fun PipelineContext<*, ApplicationCall>.getCurrentIdentity(userRepository: UserRepository): UserIdentity {
    val session = call.sessions.get<TypingTestSession>()!!
    val user = userRepository.findById(session.userId)!!
    return UserIdentity(user, session.userSessionId)
}

fun PipelineContext<*, ApplicationCall>.getCurrentUser(userRepository: UserRepository): User {
    val session = call.sessions.get<TypingTestSession>()!!
    return userRepository.findById(session.userId)!!
}

fun PipelineContext<*, ApplicationCall>.clearUserSession() {
    call.sessions.clear<TypingTestSession>()
}