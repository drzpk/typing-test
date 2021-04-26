package dev.drzepka.typing.server.domain.util

import dev.drzepka.typing.server.domain.exception.ValidationException

class ValidationErrors {

    val errors = ArrayList<Error>()

    fun addFieldError(field: String, message: String) {
        errors.add(FieldError(field, message))
    }

    fun addObjectError(message: String) {
        errors.add(ObjectError(message))
    }

    fun verify() {
        if (errors.isNotEmpty())
            throw ValidationException(this)
    }
}

sealed class Error(val message: String)

class ObjectError(message: String) : Error(message)

class FieldError(val field: String, message: String) : Error(message)