package dev.drzepka.typing.server.application.dto.testresult

import java.time.Instant

class TestBestResultResource(
    var userDisplayName: String,
    var testCreatedAt: Instant,
    var speed: Float,
    var accuracy: Float,
    /** Total score of this result, calculated based on the speed and the accuracy. **/
    var score: Int
)