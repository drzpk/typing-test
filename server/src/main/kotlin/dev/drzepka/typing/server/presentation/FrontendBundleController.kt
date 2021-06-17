package dev.drzepka.typing.server.presentation

import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.frontendBundleController() {
    get("/") {
        call.respondRedirect("/app")
    }

    get("/app") {
        call.respondRedirect("/app/index.html")
    }

    static("css") {
        resources("frontend-bundle/css")
    }

    static("js") {
        resources("frontend-bundle/js")
    }

    static("img") {
        resources("frontend-bundle/img")
    }

    static("app") {
        resources("frontend-bundle")
    }
}