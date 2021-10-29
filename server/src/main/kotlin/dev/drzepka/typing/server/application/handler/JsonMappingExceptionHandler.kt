package dev.drzepka.typing.server.application.handler

import com.fasterxml.jackson.databind.JsonMappingException
import dev.drzepka.typing.server.application.exception.ErrorCode

class JsonMappingExceptionHandler : ExceptionHandler<JsonMappingException> {
    override fun handle(exception: JsonMappingException): HandlerResult {
        val additionalData = mapOf<String, Any>(
            "serverMessage" to (exception.message ?: "")
        )
        return ErrorCode.REQUEST_SYNTAX_INCORRECT.getException(null, additionalData).toHandlerResult()
    }
}