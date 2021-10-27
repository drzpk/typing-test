package dev.drzepka.typing.server.application.service

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import dev.drzepka.typing.server.application.dto.word.ExportedWordDTO
import dev.drzepka.typing.server.domain.entity.Word
import dev.drzepka.typing.server.domain.repository.WordRepository
import dev.drzepka.typing.server.domain.util.Logger
import java.io.OutputStream

class WordExportService(private val wordRepository: WordRepository) {

    private val log by Logger()
    private val objectMapper = ObjectMapper()

    fun exportWordList(id: Int, output: OutputStream) {
        log.info("Exporting word list {}", id)

        val generator = objectMapper.factory.createGenerator(output)
        writeWordListStart(generator)

        val sequence = wordRepository.findAll(id, true)
        sequence.forEach {
            val dto = convertToDTO(it)
            writeWord(dto, generator)
        }

        writeWordListEnd(generator)
        generator.close()

        log.info("Word list {} export complete", id)
    }

    private fun writeWordListStart(generator: JsonGenerator) {
        generator.writeStartObject()
        generator.writeFieldName(WORD_LIST_NAME)
        generator.writeStartArray()
    }

    private fun convertToDTO(word: Word): ExportedWordDTO = ExportedWordDTO(word.word, word.popularity)

    private fun writeWord(word: ExportedWordDTO, generator: JsonGenerator) {
        objectMapper.writeValue(generator, word)
    }

    private fun writeWordListEnd(generator: JsonGenerator) {
        generator.writeEndArray()
        generator.writeEndObject()
    }

    companion object {
        private const val WORD_LIST_NAME = "words"
    }
}