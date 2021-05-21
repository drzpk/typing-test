package dev.drzepka.typing.server.application.handler

import dev.drzepka.typing.server.application.dto.ErrorCodeDTO
import dev.drzepka.typing.server.application.exception.ErrorCodeException

class ErrorCodeExceptionHandler : ExceptionHandler<ErrorCodeException> {

    override fun handle(exception: ErrorCodeException): HandlerResult {
        val dto = ErrorCodeDTO(exception.code.name, exception.message, exception.`object`, exception.additionalData)
        return HandlerResult(exception.statusCode, dto)
    }


}