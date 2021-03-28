package dev.drzepka.typing.server.presentation

import dev.drzepka.typing.server.application.TypingTestSession
import dev.drzepka.typing.server.application.dto.LoginDataDTO
import dev.drzepka.typing.server.domain.dto.UserAuthenticationDetailsDTO
import dev.drzepka.typing.server.domain.service.UserService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.get

// todo: error handling
fun Route.loginController() {
    val userService = get<UserService>()

    post("login") {
        val loginData = call.receive<LoginDataDTO>()
        val dto = transaction {
            val user = userService.findUser(loginData.email, loginData.password) ?: error("user not found")
            call.sessions.set(TypingTestSession(user.id.value))
            UserAuthenticationDetailsDTO.fromUserEntity(user)
        }
        call.respond(dto)
    }

    post("logout") {
        val session = call.sessions.get<TypingTestSession>()
        if (session != null) {
            call.sessions.clear<TypingTestSession>()
            call.respond(HttpStatusCode.NoContent)
        } else {
            call.respond(HttpStatusCode.BadRequest, "no session found")
        }
    }
}