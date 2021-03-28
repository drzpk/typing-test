package dev.drzepka.typing.server

import dev.drzepka.typing.server.application.TypingTestSession
import dev.drzepka.typing.server.application.configuration.setupRouting
import dev.drzepka.typing.server.application.configuration.typingServerKoinModule
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.sessions.*
import org.koin.ktor.ext.Koin
import java.io.File

fun Application.typingTestServer() {
    install(ContentNegotiation) {
        jackson {}
    }

    install(Sessions) {
        cookie<TypingTestSession>("SESSION", directorySessionStorage(File(".sessions"), cached = true))
    }

    install(Koin) {
        modules(typingServerKoinModule())
    }

    setupRouting()
}