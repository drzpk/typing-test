package dev.drzepka.typing.server.application.handler

import dev.drzepka.typing.server.domain.util.Logger
import io.ktor.http.*

class UnknownExceptionHandler : ExceptionHandler<Exception> {
    val log by Logger()

    override fun handle(exception: Exception): HandlerResult {
        log.error("Unhandled excetion", exception)
        return HandlerResult(HttpStatusCode.InternalServerError)
    }

}