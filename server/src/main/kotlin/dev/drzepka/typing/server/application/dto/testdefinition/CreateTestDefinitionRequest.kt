package dev.drzepka.typing.server.application.dto.testdefinition

class CreateTestDefinitionRequest {
    var name = ""
    var wordListId = 0
    var duration: Int? = null
    var isActive = false
}