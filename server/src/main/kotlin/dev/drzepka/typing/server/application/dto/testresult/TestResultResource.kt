package dev.drzepka.typing.server.application.dto.testresult

import dev.drzepka.typing.server.domain.entity.Test
import dev.drzepka.typing.server.domain.entity.TestResult

data class TestResultResource(
    var id: Int,
    var correctWords: Int,
    var incorrectWords: Int,
    var correctKeystrokes: Int,
    var incorrectKeystrokes: Int,
    var accuracy: Float,
    var wordsPerMinute: Float,
    var durationSeconds: Int
) {

    companion object {
        fun fromEntity(resultEntity: TestResult, testEntity: Test): TestResultResource {
            return TestResultResource(
                resultEntity.id!!,
                resultEntity.correctWords,
                resultEntity.incorrectWords,
                resultEntity.correctKeystrokes,
                resultEntity.incorrectKeystrokes,
                resultEntity.accuracy,
                resultEntity.wordsPerMinute,
                testEntity.duration.seconds.toInt()
            )
        }
    }
}