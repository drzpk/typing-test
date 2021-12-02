package dev.drzepka.typing.server.presentation

import dev.drzepka.typing.server.application.TypingTestSession
import dev.drzepka.typing.server.application.dto.LoginDataDTO
import dev.drzepka.typing.server.application.dto.user.CreateUserRequest
import dev.drzepka.typing.server.application.dto.user.UserAuthenticationDetailsDTO
import dev.drzepka.typing.server.application.service.ApplicationSessionService
import dev.drzepka.typing.server.application.service.TestManagerService
import dev.drzepka.typing.server.application.service.UserService
import dev.drzepka.typing.server.application.util.clearUserSession
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.get

fun Route.loginController() {
    val userService = get<UserService>()
    val applicationSessionService = get<ApplicationSessionService>()
    val testService = get<TestManagerService>()

    post("register") {
        val request = call.receive<CreateUserRequest>()
        val resource = transaction {
            userService.createUser(request)
        }
        call.respond(resource)
    }

    post("login") {
        val loginData = call.receive<LoginDataDTO>()
        val dto = transaction {
            val user = userService.findUser(loginData.email, loginData.password) ?: error("user not found")

            val session = applicationSessionService.createApplicationSession(user, call)
            val oldSession = call.sessions.get<TypingTestSession>()!!
            call.sessions.set(session)

            testService.assignAnonymousTestsInSessionToUser(oldSession.userSessionId, user)
            UserAuthenticationDetailsDTO.fromUserEntity(user)
        }
        call.respond(dto)
    }

    post("logout") {
        val session = call.sessions.get<TypingTestSession>()
        if (session != null) {
            clearUserSession()
            call.respond(HttpStatusCode.NoContent)
        } else {
            call.respond(HttpStatusCode.BadRequest, "no session found")
        }
    }
}