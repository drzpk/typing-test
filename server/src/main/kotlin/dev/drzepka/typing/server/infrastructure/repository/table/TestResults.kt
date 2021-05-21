package dev.drzepka.typing.server.infrastructure.repository.table

import org.jetbrains.exposed.dao.id.IntIdTable

object TestResults : IntIdTable("test_results") {
    var test = reference("test", Tests)

    var correctWords = integer("correct_words")
    var incorrectWords = integer("incorrect_words")

    var correctKeystrokes = integer("correct_keystrokes")
    var incorrectKeystrokes = integer("incorrect_keystrokes")

    var accuracy = float("accuracy")
    var wordsPerMinute = float("words_per_minute")
}