package dev.drzepka.typing.server.domain.util

import dev.drzepka.typing.server.application.TypingTestSession
import dev.drzepka.typing.server.domain.entity.User
import io.ktor.application.*
import io.ktor.sessions.*
import io.ktor.util.pipeline.*

fun PipelineContext<*, ApplicationCall>.getCurrentUser(): User {
    val session = call.sessions.get<TypingTestSession>()!!
    return User.findById(session.userId)!!
}