package dev.drzepka.typing.server.domain.repository

import dev.drzepka.typing.server.domain.TestConstants
import java.time.Duration

interface ConfigurationRepository {
    /**
     * Maximum delay after test creation within which it must be started.
     */
    fun testStartTimeLimit(): Duration = Duration.ofMinutes(5)

    /**
     * Maximum delay, starting from test supposed end time (start + duration), after which it must be finished.
     */
    fun testFinishTimeLimit(): Duration = Duration.ofSeconds(15)

    /**
     * Maximum theoretical words-per-minute speed that can be acheived by the user.
     * Used to adjust amount of generated words for the tests.
     */
    fun maxWPM(): Int = 250

    /**
     * Minimum speed accepted by the application. Everything below will be rejected.
     */
    fun minWPM(): Int = 15

    /**
     * Similar to [maxWPM], but expressed in characters-per-minute.
     */
    fun maxCPM(): Int = maxWPM() * TestConstants.CHARACTERS_PER_WORD
}