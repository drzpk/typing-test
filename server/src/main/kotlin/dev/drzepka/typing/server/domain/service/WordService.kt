package dev.drzepka.typing.server.domain.service

import dev.drzepka.typing.server.domain.Page
import dev.drzepka.typing.server.domain.dto.word.AddWordRequest
import dev.drzepka.typing.server.domain.dto.word.ListWordsRequest
import dev.drzepka.typing.server.domain.entity.Word
import dev.drzepka.typing.server.domain.entity.WordList
import dev.drzepka.typing.server.domain.entity.table.WordsTable
import dev.drzepka.typing.server.domain.util.ValidationErrors
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and

class WordService {

    fun addWord(request: AddWordRequest): Word {
        val wordList = WordList.findById(request.wordListId)

        validateAddWord(request, wordList)

        return Word.new {
            word = request.word
            popularity = request.popularity
            this.wordList = wordList!!
        }
    }

    fun listWords(request: ListWordsRequest): Page<Word> {
        val allElements = Word.count(WordsTable.wordList eq request.wordListId)
        val words = Word
            .find { WordsTable.wordList eq request.wordListId }
            .orderBy(WordsTable.popularity to SortOrder.DESC)
            .limit(request.size, (request.page - 1) * request.size.toLong())

        return Page(words.toList(), request.page, request.size, allElements)
    }

    fun deleteWord(wordId: Int): Boolean {
        val word = Word.findById(wordId)
        return if (word != null) {
            word.delete()
            true
        } else {
            false
        }
    }

    private fun validateAddWord(request: AddWordRequest, wordList: WordList?) {
        val validation = ValidationErrors()
        if (wordList == null)
            validation.addFieldError("wordListId", "Word list with id ${request.wordListId} wasn't found.")
        if (request.popularity < 1)
            validation.addFieldError("popularity", "Popularity must be a positive integer.")
        if (request.word.isEmpty() || request.word.length > 64)
            validation.addFieldError("word", "Word length must be between 1 and 64 characters.")

        if (request.word.isNotEmpty()) {
            val count =
                Word.find { (WordsTable.wordList eq request.wordListId) and (WordsTable.word eq request.word) }.count()
            if (count > 0)
                validation.addFieldError("word", "Word '${request.word}' already exists for given list.")
        }

        validation.verify()
    }
}