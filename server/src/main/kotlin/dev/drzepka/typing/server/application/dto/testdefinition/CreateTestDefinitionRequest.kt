package dev.drzepka.typing.server.application.dto.testdefinition

data class CreateTestDefinitionRequest(
    var name: String = "",
    var wordListId: Int = 0,
    var duration: Int? = null,
    var isActive: Boolean = false
)