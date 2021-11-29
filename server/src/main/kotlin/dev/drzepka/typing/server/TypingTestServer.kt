package dev.drzepka.typing.server

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import dev.drzepka.typing.server.application.TypingTestSession
import dev.drzepka.typing.server.application.configuration.setupRouting
import dev.drzepka.typing.server.application.configuration.setupStatusPages
import dev.drzepka.typing.server.application.configuration.typingServerKoinModule
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.sessions.*
import org.koin.ktor.ext.Koin
import java.io.File
import java.time.Duration

fun Application.typingTestServer() {
    install(ContentNegotiation) {
        jackson {
            registerModule(JavaTimeModule())
        }
    }

    install(Sessions) {
        cookie<TypingTestSession>("SESSION", directorySessionStorage(File(".sessions"), cached = true)) {
            cookie.maxAgeInSeconds = Duration.ofDays(10).seconds
        }
    }

    install(Koin) {
        modules(typingServerKoinModule())
    }

    install(XForwardedHeaderSupport)

    setupRouting()
    setupStatusPages()
}