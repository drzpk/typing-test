package dev.drzepka.typing.server.infrastructure.repository.table

import dev.drzepka.typing.server.domain.value.TestState
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.timestamp

object Tests : IntIdTable("tests") {
    var testDefinition = reference("test_definition", TestDefinitions)
    var user = reference("user", Users)
    var state = enumerationByName("state", 32, TestState::class)

    var createdAt = timestamp("created_at")
    var startedAt = timestamp("started_at").nullable()
    var finishedAt = timestamp("finished_at").nullable()

    var startTimeLimit = integer("start_time_limit").nullable()
    var finishTimeLimit = integer("finish_time_limit").nullable()

    var selectedWords = blob("selected_words")
    var enteredWords = blob("entered_words").nullable()

    var backspaceCount = integer("backspace_count").nullable()
    var wordRegenerationCount = integer("word_regeneration_count")
}