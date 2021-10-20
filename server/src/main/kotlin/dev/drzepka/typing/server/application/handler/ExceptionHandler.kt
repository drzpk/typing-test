package dev.drzepka.typing.server.application.handler

import io.ktor.http.*

interface ExceptionHandler<T : Exception> {
    fun handle(exception: T): HandlerResult
}

data class HandlerResult(val statusCode: HttpStatusCode = HttpStatusCode.OK, val body: Any? = null)