package dev.drzepka.typing.server.application.exception

import dev.drzepka.typing.server.application.validation.ValidationState

class ValidationException private constructor(message: String, val validationErrors: ValidationState) : RuntimeException(message) {
    companion object {
        fun fromValidationState(validationState: ValidationState): ValidationException {
            return ValidationException(createMessage(validationState), validationState)
        }

        private fun createMessage(validationState: ValidationState): String {
            val builder = StringBuilder()
            builder.append("There are validation errors:")

            validationState.errors.forEachIndexed { index, error ->
                builder.appendLine()
                builder.append(index + 1)
                builder.append(". ")
                builder.append(error.toLogString())
            }

            builder.appendLine()
            return builder.toString()
        }
    }
}