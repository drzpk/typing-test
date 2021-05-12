package dev.drzepka.typing.server.application.dto.testdefinition

import dev.drzepka.typing.server.application.dto.wordlist.WordListResource
import dev.drzepka.typing.server.domain.entity.TestDefinition
import java.time.Instant

data class TestDefinitionResource(
    var id: Int,
    var name: String,
    var wordList: WordListResource,
    var duration: Int,
    var isActive: Boolean,
    var createdAt: Instant,
    var modifiedAt: Instant
) {

    companion object {
        fun fromEntity(entity: TestDefinition): TestDefinitionResource {
            return TestDefinitionResource(
                entity.id!!,
                entity.name,
                WordListResource.fromEntity(entity.wordList),
                entity.duration.seconds.toInt(),
                entity.isActive,
                entity.createdAt,
                entity.modifiedAt
            )
        }
    }
}