package dev.drzepka.typing.server.application.dto

class ValidationErrorsDTO {
    val errors = ArrayList<ValidationErrorDTO>()
}

class ValidationErrorDTO {
    var type = ""
    var field: String? = null
    var message = ""
}