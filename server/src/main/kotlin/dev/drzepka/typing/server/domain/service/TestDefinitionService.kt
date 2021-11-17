package dev.drzepka.typing.server.domain.service

import dev.drzepka.typing.server.domain.entity.TestDefinition
import dev.drzepka.typing.server.domain.exception.FixedTextTooShortException
import dev.drzepka.typing.server.domain.repository.ConfigurationRepository
import dev.drzepka.typing.server.domain.util.Logger
import dev.drzepka.typing.server.domain.util.Mockable
import kotlin.math.ceil

@Mockable
class TestDefinitionService(private val configurationRepository: ConfigurationRepository) {

    private val log by Logger()

    /**
     * Checks if word list's fixed text is long enough to be used with given test definition.
     * @throws FixedTextTooShortException if word list's text is too short
     */
    fun checkIfFixedTextIsLongEnough(definition: TestDefinition) {
        val wordList = definition.wordList
        if (wordList.text == null) {
            log.debug("Skipping checking text length - word list text is null")
            return
        }

        val duration = definition.duration
        if (duration == null) {
            log.debug("Skipping checking text length - test definition duration is null")
            return
        }

        val testDurationMinutes = duration.seconds / 60.0
        val minimumAcceptableLength = ceil(configurationRepository.maxCPM() * testDurationMinutes).toInt()
        val textLength = wordList.text!!.toText().length

        if (textLength < minimumAcceptableLength)
            throw FixedTextTooShortException(definition.id!!, wordList.id!!, textLength, minimumAcceptableLength)
    }
}