package dev.drzepka.typing.server.domain.dto

import java.time.Instant

class TestResultDataDTO(
    var testResultId: Int,
    var userDisplayName: String,
    var testStartedAt: Instant,
    var testFinishedAt: Instant,
    var testDuration: Int?,
    var speed: Float,
    var accuracy: Float
)