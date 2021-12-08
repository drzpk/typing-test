package dev.drzepka.typing.server.application.dto.teststats

import dev.drzepka.typing.server.application.dto.testdefinition.TestDefinitionResource

@Suppress("unused")
class TestStatsResource(
    var testDefinition: TestDefinitionResource,
    var finishedTests: Int,
    var userStats: StatsGroup,
    val globalStats: StatsGroup,
    var speedValues: Collection<TimedData<Float>>,
    var accuracyValues: Collection<TimedData<Float>>
) {
    class StatsGroup(
        var averageSpeed: Float,
        var averageAccuracy: Float
    )
}