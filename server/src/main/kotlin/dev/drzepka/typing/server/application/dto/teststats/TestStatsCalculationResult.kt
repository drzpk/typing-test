package dev.drzepka.typing.server.application.dto.teststats

data class TestStatsCalculationResult(
    val takenTests: Int,
    val averageSpeed: Float,
    val averageAccuracy: Float,
    val speedValues: List<TimedData<Float>>,
    val accuracyValues: List<TimedData<Float>>
)