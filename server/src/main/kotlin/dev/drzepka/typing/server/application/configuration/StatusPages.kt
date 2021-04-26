package dev.drzepka.typing.server.application.configuration

import dev.drzepka.typing.server.application.handler.ValidationExceptionHandler
import dev.drzepka.typing.server.domain.exception.ValidationException
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*

fun Application.setupStatusPages() {
    install(StatusPages) {

        val validationExceptionHandler = ValidationExceptionHandler()

        exception<ValidationException> { cause ->
            val result = validationExceptionHandler.handle(cause)
            if (result.body != null)
                call.respond(result.statusCode, result.body)
            else
                call.respond(result.statusCode)
        }
    }
}
