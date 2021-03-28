package dev.drzepka.typing.server.application.configuration

import dev.drzepka.typing.server.application.TypingTestSession
import dev.drzepka.typing.server.domain.service.UserService
import dev.drzepka.typing.server.presentation.loginController
import dev.drzepka.typing.server.presentation.userController
import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.util.pipeline.*
import org.koin.experimental.property.inject
import org.koin.ktor.ext.get

fun Application.setupRouting() {

    routing {

        route("/api") {
            loginController()
            route("/") {
                intercept(ApplicationCallPipeline.Features) {
                    val session = call.sessions.get<TypingTestSession>() ?: error("Session not found")
                }

                userController()
            }
        }
    }
}