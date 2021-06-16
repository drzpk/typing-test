package dev.drzepka.typing.server.application.handler

import dev.drzepka.typing.server.application.exception.SecurityException
import dev.drzepka.typing.server.domain.util.Logger
import io.ktor.http.*

class SecurityExceptionHandler : ExceptionHandler<SecurityException> {

    private val log by Logger()

    override fun handle(exception: SecurityException): HandlerResult {
        log.error("Security exception", exception)
        return HandlerResult(HttpStatusCode.Forbidden, null)
    }
}