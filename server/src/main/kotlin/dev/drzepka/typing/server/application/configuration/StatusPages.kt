package dev.drzepka.typing.server.application.configuration

import dev.drzepka.typing.server.application.handler.*
import dev.drzepka.typing.server.application.handler.domain.FixedTextTooShortExceptionHandler
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*

fun Application.setupStatusPages() {
    install(StatusPages) {
        // Application exceptions
        addExceptionHandler(ValidationExceptionHandler())
        addExceptionHandler(ErrorCodeExceptionHandler())
        addExceptionHandler(JsonMappingExceptionHandler())
        addExceptionHandler(UnrecognizedPropertyExceptionHandler())
        addExceptionHandler(SecurityExceptionHandler())

        // Domain exceptions
        addExceptionHandler(FixedTextTooShortExceptionHandler())

        // Other cases
        addExceptionHandler(UnknownExceptionHandler())
    }
}

inline fun <reified T : Exception> StatusPages.Configuration.addExceptionHandler(handler: ExceptionHandler<T>) {
    exception<T> { cause ->
        val result = handler.handle(cause)
        if (result.body != null)
            call.respond(result.statusCode, result.body)
        else
            call.respond(result.statusCode)
    }
}