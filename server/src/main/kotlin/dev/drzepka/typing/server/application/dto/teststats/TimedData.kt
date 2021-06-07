package dev.drzepka.typing.server.application.dto.teststats

import java.time.Instant

data class TimedData<T>(
    var timestamp: Instant,
    var value: T
)