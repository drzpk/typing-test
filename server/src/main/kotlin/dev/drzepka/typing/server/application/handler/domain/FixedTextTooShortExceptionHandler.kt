package dev.drzepka.typing.server.application.handler.domain

import dev.drzepka.typing.server.application.exception.ErrorCode
import dev.drzepka.typing.server.application.handler.ExceptionHandler
import dev.drzepka.typing.server.application.handler.HandlerResult
import dev.drzepka.typing.server.application.handler.toHandlerResult
import dev.drzepka.typing.server.domain.exception.FixedTextTooShortException

class FixedTextTooShortExceptionHandler : ExceptionHandler<FixedTextTooShortException> {
    override fun handle(exception: FixedTextTooShortException): HandlerResult {
        val additionalData = mapOf(
            "testDefinition" to exception.testDefinition,
            "wordListId" to exception.wordListId,
            "length" to exception.length,
            "minimumLength" to exception.minimumLength
        )

        return ErrorCode.FIXED_TEXT_TOO_SHORT.getException(null, additionalData, exception.message).toHandlerResult()
    }
}