package dev.drzepka.typing.server.application.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import dev.drzepka.typing.server.application.dto.word.ExportedWordDTO
import dev.drzepka.typing.server.domain.entity.Word
import dev.drzepka.typing.server.domain.repository.WordRepository
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.ByteArrayOutputStream

@ExtendWith(MockitoExtension::class)
internal class WordExportServiceTest {

    private val wordRepository = mock<WordRepository>()

    private val objectMapper = ObjectMapper().apply {
        registerKotlinModule()
    }

    @Test
    fun `should export word list`() {
        val words = listOf(
            getWord("first", 10),
            getWord("second", 20)
        )

        whenever(wordRepository.findAll(any(), any())).thenReturn(words.asSequence())

        val outputStream = ByteArrayOutputStream()
        getService().exportWordList(1, outputStream)

        val data = objectMapper.readValue<ExportedData>(outputStream.toByteArray())
        then(data.words).hasSize(2)

        val first = data.words[0]
        then(first.word).isEqualTo("first")
        then(first.popularity).isEqualTo(10)

        val second = data.words[1]
        then(second.word).isEqualTo("second")
        then(second.popularity).isEqualTo(20)
    }

    private fun getWord(word: String, popularity: Int): Word = Word().apply {
        this.word = word
        this.popularity = popularity
    }

    private fun getService(): WordExportService = WordExportService(wordRepository)

    private class ExportedData {
        var words: List<ExportedWordDTO> = emptyList()
    }
}