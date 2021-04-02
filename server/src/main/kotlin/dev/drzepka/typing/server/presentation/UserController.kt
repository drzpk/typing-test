package dev.drzepka.typing.server.presentation

import dev.drzepka.typing.server.application.TypingTestSession
import dev.drzepka.typing.server.domain.dto.UserAuthenticationDetailsDTO
import dev.drzepka.typing.server.domain.entity.User
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.userController() {
    get("/authentication-details") {
        val session = call.sessions.get<TypingTestSession>()!!
        val dto = transaction {
            val user = User.findById(session.userId)
            UserAuthenticationDetailsDTO.fromUserEntity(user!!)
        }
        call.respond(dto)
    }
}