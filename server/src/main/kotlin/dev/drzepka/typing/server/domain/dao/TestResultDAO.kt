package dev.drzepka.typing.server.domain.dao

import dev.drzepka.typing.server.domain.dto.TestResultDataDTO

interface TestResultDAO {
    fun findHighestResultsBySpeed(testDefinitionId: Int, limit: Int): List<TestResultDataDTO>
    fun findHighestResultsByAccuracy(testDefinitionId: Int, limit: Int): List<TestResultDataDTO>
}