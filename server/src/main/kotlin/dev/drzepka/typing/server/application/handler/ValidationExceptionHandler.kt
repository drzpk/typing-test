package dev.drzepka.typing.server.application.handler

import dev.drzepka.typing.server.application.dto.ValidationErrorDTO
import dev.drzepka.typing.server.application.dto.ValidationErrorsDTO
import dev.drzepka.typing.server.application.exception.ValidationException
import dev.drzepka.typing.server.application.validation.Error
import dev.drzepka.typing.server.application.validation.FieldError
import dev.drzepka.typing.server.application.validation.ObjectError
import io.ktor.http.*

class ValidationExceptionHandler : ExceptionHandler<ValidationException> {

    override fun handle(exception: ValidationException): HandlerResult {
        val errors = ValidationErrorsDTO()
        errors.errors.addAll(exception.validationErrors.errors.map { convertError(it) })

        return HandlerResult(HttpStatusCode.UnprocessableEntity, errors)
    }

    private fun convertError(error: Error): ValidationErrorDTO {
        return when (error) {
            is ObjectError -> {
                ValidationErrorDTO().apply {
                    type = "object"
                    message =  error.message
                }
            }
            is FieldError -> {
                ValidationErrorDTO().apply {
                    type = "field"
                    field = error.field
                    message = error.message
                }
            }
        }
    }
}