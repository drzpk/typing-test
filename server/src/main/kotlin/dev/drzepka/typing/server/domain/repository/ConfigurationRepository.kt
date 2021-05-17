package dev.drzepka.typing.server.domain.repository

import java.time.Duration

interface ConfigurationRepository {
    /**
     * Maximum delay after test creation within which it must be started.
     */
    fun testStartTimeLimit(): Duration = Duration.ofMinutes(5)

    /**
     * Maximum delay after test start within which it must be finished.
     */
    fun testFinishTimeLimit(): Duration = Duration.ofSeconds(15)

    /**
     * Maximum theoretical words-per-minute speed that can be acheived by the user.
     * Used to adjust amount of generated words for the tests.
     */
    fun maxWPM(): Int = 250
}