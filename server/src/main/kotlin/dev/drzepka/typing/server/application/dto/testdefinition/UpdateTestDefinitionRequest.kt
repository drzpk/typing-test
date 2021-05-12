package dev.drzepka.typing.server.application.dto.testdefinition

class UpdateTestDefinitionRequest {
    var id = 0
    var name: String? = null
    var wordListId: Int? = null
    var duration: Int? = null
    var isActive: Boolean? = null
}