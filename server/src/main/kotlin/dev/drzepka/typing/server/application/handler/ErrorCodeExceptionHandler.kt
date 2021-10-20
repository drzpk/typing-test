package dev.drzepka.typing.server.application.handler

import dev.drzepka.typing.server.application.dto.ErrorCodeDTO
import dev.drzepka.typing.server.application.exception.ErrorCodeException

class ErrorCodeExceptionHandler : ExceptionHandler<ErrorCodeException> {
    override fun handle(exception: ErrorCodeException): HandlerResult = exception.toHandlerResult()
}

fun ErrorCodeException.toHandlerResult(): HandlerResult {
    val dto = ErrorCodeDTO(code.name, message, `object`, additionalData)
    return HandlerResult(statusCode, dto)
}