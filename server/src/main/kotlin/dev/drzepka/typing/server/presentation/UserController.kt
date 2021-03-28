package dev.drzepka.typing.server.presentation

import io.ktor.routing.*

fun Route.userController() {
    get("/current-user") {
        println("test")
    }
}