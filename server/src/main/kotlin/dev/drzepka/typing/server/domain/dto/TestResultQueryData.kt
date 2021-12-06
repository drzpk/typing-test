package dev.drzepka.typing.server.domain.dto

import java.time.temporal.TemporalAmount

data class TestResultQueryData(val testDefinitionId: Int, val range: TemporalAmount?, val limit: Int)