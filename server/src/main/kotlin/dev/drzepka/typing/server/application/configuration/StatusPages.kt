package dev.drzepka.typing.server.application.configuration

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import dev.drzepka.typing.server.application.exception.ValidationException
import dev.drzepka.typing.server.application.handler.UnrecognizedPropertyExceptionHandler
import dev.drzepka.typing.server.application.handler.ValidationExceptionHandler
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import org.slf4j.LoggerFactory

fun Application.setupStatusPages() {
    val log = LoggerFactory.getLogger("StatusPages")

    install(StatusPages) {

        val validationExceptionHandler = ValidationExceptionHandler()
        val unrecognizedPropertyExceptionhandler = UnrecognizedPropertyExceptionHandler()

        exception<ValidationException> { cause ->
            val result = validationExceptionHandler.handle(cause)
            if (result.body != null)
                call.respond(result.statusCode, result.body)
            else
                call.respond(result.statusCode)
        }

        exception<UnrecognizedPropertyException> { cause ->
            val result = unrecognizedPropertyExceptionhandler.handle(cause)
            if (result.body != null)
                call.respond(result.statusCode, result.body)
            else
                call.respond(result.statusCode)
        }

        exception<Throwable> { cause ->
            log.error("Unhandled excetion", cause)
        }
    }
}
