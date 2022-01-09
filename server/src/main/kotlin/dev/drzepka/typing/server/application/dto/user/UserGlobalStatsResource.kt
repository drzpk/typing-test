package dev.drzepka.typing.server.application.dto.user

data class UserGlobalStatsResource(
    val completedTests: Int,
    val testsPerDay: Float
)