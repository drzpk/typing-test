package dev.drzepka.typing.server.application.configuration

import dev.drzepka.typing.server.application.security.sessionInterceptor
import dev.drzepka.typing.server.presentation.*
import io.ktor.application.*
import io.ktor.routing.*

fun Application.setupRouting() {

    routing {

        route("/api") {
            loginController()

            route("/") {
                sessionInterceptor()

                userController()
                wordListController()
                testDefinitionController()
                testController()
                testResultController()
            }
        }
    }
}