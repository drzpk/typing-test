package dev.drzepka.typing.server.application.configuration

import dev.drzepka.typing.server.application.TypingTestSession
import dev.drzepka.typing.server.presentation.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

fun Application.setupRouting() {

    routing {

        route("/api") {
            loginController()
            route("/") {
                intercept(ApplicationCallPipeline.Features) {
                    val session = call.sessions.get<TypingTestSession>()
                    if (session == null) {
                        call.respond(HttpStatusCode.Unauthorized)
                        finish()
                    }
                }

                userController()
                // TODO: check admin privileges
                wordListController()
                testDefinitionController()
                testController()
            }
        }
    }
}