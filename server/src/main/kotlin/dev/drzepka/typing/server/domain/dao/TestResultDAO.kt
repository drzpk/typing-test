package dev.drzepka.typing.server.domain.dao

import dev.drzepka.typing.server.domain.dto.TestResultDataDTO
import dev.drzepka.typing.server.domain.dto.TestResultQueryData

interface TestResultDAO {
    fun findHighestResultsBySpeed(query: TestResultQueryData): List<TestResultDataDTO>
    fun findHighestResultsByAccuracy(query: TestResultQueryData): List<TestResultDataDTO>
}