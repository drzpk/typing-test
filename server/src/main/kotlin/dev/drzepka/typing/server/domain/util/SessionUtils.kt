package dev.drzepka.typing.server.domain.util

import dev.drzepka.typing.server.application.TypingTestSession
import dev.drzepka.typing.server.domain.entity.User
import dev.drzepka.typing.server.domain.repository.UserRepository
import io.ktor.application.*
import io.ktor.sessions.*
import io.ktor.util.pipeline.*

fun PipelineContext<*, ApplicationCall>.getCurrentUser(userRepository: UserRepository): User {
    val session = call.sessions.get<TypingTestSession>()!!
    return userRepository.findById(session.userId)!!
}