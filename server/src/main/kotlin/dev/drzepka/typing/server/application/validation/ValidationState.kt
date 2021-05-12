package dev.drzepka.typing.server.application.validation

import dev.drzepka.typing.server.application.exception.ValidationException
import sun.plugin.dom.exception.InvalidStateException

class ValidationState {

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

    companion object {
        fun throwFieldError(field: String, message: String): Nothing {
            val validation = ValidationState()
            validation.addFieldError(field, message)
            validation.verify()
            throw InvalidStateException("")
        }
    }
}

sealed class Error(val message: String)

class ObjectError(message: String) : Error(message)

class FieldError(val field: String, message: String) : Error(message)