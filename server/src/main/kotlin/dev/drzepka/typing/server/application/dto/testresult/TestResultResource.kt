package dev.drzepka.typing.server.application.dto.testresult

import dev.drzepka.typing.server.domain.entity.TestResult

data class TestResultResource(
    var id: Int,
    var correctWords: Int,
    var incorrectWords: Int,
    var correctKeystrokes: Int,
    var incorrectKeystrokes: Int,
    var accuracy: Float,
    var wordsPerMinute: Float
) {

    companion object {
        fun fromEntity(entity: TestResult): TestResultResource {
            return TestResultResource(
                entity.id!!,
                entity.correctWords,
                entity.incorrectWords,
                entity.correctKeystrokes,
                entity.incorrectKeystrokes,
                entity.accuracy,
                entity.wordsPerMinute
            )
        }
    }
}