package dev.drzepka.typing.server.application.dto.testresult

import java.time.Instant

class TestBestResultResource(
    var userDisplayName: String,
    var testCreatedAt: Instant,
    var speed: Float,
    var accuracy: Float,
    var score: Int
)