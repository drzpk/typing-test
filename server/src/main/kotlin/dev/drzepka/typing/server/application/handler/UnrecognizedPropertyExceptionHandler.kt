package dev.drzepka.typing.server.application.handler

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import dev.drzepka.typing.server.application.dto.UnknownFieldErrorDTO
import io.ktor.http.*

class UnrecognizedPropertyExceptionHandler : ExceptionHandler<UnrecognizedPropertyException> {

    @Suppress("UNCHECKED_CAST")
    override fun handle(exception: UnrecognizedPropertyException): HandlerResult {
        val error = UnknownFieldErrorDTO(exception.propertyName, exception.knownPropertyIds as Collection<String>)
        return HandlerResult(HttpStatusCode.BadRequest, error)
    }
}