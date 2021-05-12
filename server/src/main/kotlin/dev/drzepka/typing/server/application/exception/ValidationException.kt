package dev.drzepka.typing.server.application.exception

import dev.drzepka.typing.server.application.validation.ValidationState

class ValidationException(val validationErrors: ValidationState) : RuntimeException("Validation error")