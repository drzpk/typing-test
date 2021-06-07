package dev.drzepka.typing.server.application.dto.teststats

import dev.drzepka.typing.server.application.dto.testdefinition.TestDefinitionResource

class TestStatsResource(
    var testDefinition: TestDefinitionResource,
    var finishedTests: Int,
    var averageWordsPerMinute: Float,
    var averageAccuracy: Float,
    var wordsPerMinuteValues: Collection<TimedData<Float>>,
    var accuracyValues: Collection<TimedData<Float>>
)