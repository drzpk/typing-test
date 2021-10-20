package dev.drzepka.typing.server.domain.service

import dev.drzepka.typing.server.domain.entity.TestDefinition
import dev.drzepka.typing.server.domain.exception.FixedTextTooShortException
import dev.drzepka.typing.server.domain.repository.ConfigurationRepository
import dev.drzepka.typing.server.domain.util.Mockable
import kotlin.math.ceil

@Mockable
class TestDefinitionService(private val configurationRepository: ConfigurationRepository) {

    /**
     * Checks if word list's fixed text is long enough to be used with given test definition.
     * @throws FixedTextTooShortException if word list's text is too short
     */
    fun checkIfFixedTextIsLongEnough(definition: TestDefinition) {
        val wordList = definition.wordList
        if (wordList.text == null)
            return

        val testDurationMinutes = definition.duration.seconds / 60.0
        val minimumAcceptableLength = ceil(configurationRepository.maxCPM() * testDurationMinutes).toInt()
        val textLength = wordList.text!!.toText().length

        if (textLength < minimumAcceptableLength)
            throw FixedTextTooShortException(definition.id!!, wordList.id!!, textLength, minimumAcceptableLength)
    }
}