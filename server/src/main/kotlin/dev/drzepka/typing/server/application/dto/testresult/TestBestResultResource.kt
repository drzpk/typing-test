package dev.drzepka.typing.server.application.dto.testresult

import java.time.Instant

@Suppress("unused")
class TestBestResultResource(
    var userDisplayName: String,
    var testCreatedAt: Instant,
    var speed: Float,
    var accuracy: Float,
    var durationSeconds: Int,
    /** Total score of this result, calculated based on the speed and the accuracy. **/
    var score: Int
)