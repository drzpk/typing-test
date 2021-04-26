package dev.drzepka.typing.server.domain.exception

import dev.drzepka.typing.server.domain.util.ValidationErrors

class ValidationException(val validationErrors: ValidationErrors) : RuntimeException("Validation error")