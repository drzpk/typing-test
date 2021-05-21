package dev.drzepka.typing.server.application.dto.test

import dev.drzepka.typing.server.application.dto.testdefinition.TestDefinitionResource
import dev.drzepka.typing.server.domain.entity.Test
import dev.drzepka.typing.server.domain.value.TestState
import java.time.Instant

class TestResource {
    var id = 0
    var state: State? = null
    var definition: TestDefinitionResource? = null
    var createdAt: Instant? = null
    var startedAt: Instant? = null
    var finishedAt: Instant? = null
    var selectedWords: String? = null
    var enteredWords: String? = null
    var wordRegenerationCount: Int = 0

    var startDueTime: Instant? = null
    var finishDueTime: Instant? = null

    companion object {
        fun fromEntity(entity: Test): TestResource {
            return TestResource().apply {
                id = entity.id!!
                state = State.fromValue(entity.state)
                definition = TestDefinitionResource.fromEntity(entity.testDefinition)
                createdAt = entity.createdAt
                startedAt = entity.startedAt
                finishedAt = entity.finishedAt
                selectedWords = entity.selectedWords.serialize()
                enteredWords = entity.enteredWords?.serialize()
                wordRegenerationCount = entity.wordRegenerationCount

                if (entity.startTimeLimit != null)
                    startDueTime = entity.createdAt.plus(entity.startTimeLimit)

                if (entity.startedAt != null && entity.finishTimeLimit != null)
                    finishDueTime = entity.startedAt!!.plus(entity.finishTimeLimit)
            }
        }
    }

    @Suppress("unused")
    enum class State {
        CREATED, CREATED_TIMEOUT, STARTED, STARTED_TIMEOUT, FINISHED;

        companion object {
            fun fromValue(valueState: TestState): State = valueOf(valueState.name)
        }
    }
}