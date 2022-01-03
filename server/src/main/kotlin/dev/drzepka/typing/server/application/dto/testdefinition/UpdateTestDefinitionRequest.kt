package dev.drzepka.typing.server.application.dto.testdefinition

data class UpdateTestDefinitionRequest(
    var id: Int = 0,
    var name: String? = null,
    var wordListId: Int? = null,
    var duration: Int? = null,
    var isActive: Boolean? = null
)