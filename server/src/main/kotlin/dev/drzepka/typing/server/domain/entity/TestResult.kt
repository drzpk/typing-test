package dev.drzepka.typing.server.domain.entity

import dev.drzepka.typing.server.domain.TestConstants
import dev.drzepka.typing.server.domain.value.TestState
import java.time.Duration
import kotlin.math.max

class TestResult : AbstractEntity<Int>() {
    lateinit var test: Test

    var correctWords = 0
    var incorrectWords = 0

    var correctKeystrokes = 0
    var incorrectKeystrokes = 0

    var accuracy = 0f
    var wordsPerMinute = 0f

    fun calculateResult(test: Test) {
        if (test.state != TestState.FINISHED)
            throw IllegalArgumentException("Test isn't finished.")

        resetResult()
        this.test = test
        calculateCorrectness(test)
    }

    private fun resetResult() {
        correctWords = 0
        incorrectWords = 0
        correctKeystrokes = 0
        incorrectKeystrokes = 0
        accuracy = 0f
        wordsPerMinute = 0f
    }

    private fun calculateCorrectness(test: Test) {
        for (wordNo in 0 until test.enteredWords!!.size()) {
            val selected = test.selectedWords.getWord(wordNo)
            val entered = test.enteredWords!!.getWord(wordNo)

            calculateWordCorrectness(selected, entered)
            calculateKeystrokeCorrectness(selected, entered)
        }

        calculateAccuracy(test.backspaceCount!!)
        calculateWordsPerMinute(test.duration)
    }

    private fun calculateWordCorrectness(selectedWord: String, enteredWord: String) {
        if (selectedWord == enteredWord)
            correctWords++
        else
            incorrectWords++
    }

    private fun calculateKeystrokeCorrectness(selectedWord: String, enteredWord: String) {
        val length = max(selectedWord.length, enteredWord.length)
        for (i in 0 until length) {
            val selectedChar = if (selectedWord.length > i) selectedWord[i] else null
            val enteredChar = if (enteredWord.length > i) enteredWord[i] else null

            if (selectedChar == enteredChar)
                correctKeystrokes++
            else
                incorrectKeystrokes++
        }
    }

    private fun calculateAccuracy(backspaceCount: Int) {
        val backspaceIncorrectKeystrokeValue = backspaceCount * TestConstants.BACKSPACE_INCORRECT_KEYSTROKE_WEIGHT
        val totalIncorrectKeystrokes = incorrectKeystrokes + backspaceIncorrectKeystrokeValue
        val totalKeystrokes = correctKeystrokes + totalIncorrectKeystrokes
        accuracy = correctKeystrokes / totalKeystrokes
    }

    private fun calculateWordsPerMinute(testDuration: Duration) {
        val durationSeconds = testDuration.seconds.toFloat()
        val durationMinutes = durationSeconds / 60
        wordsPerMinute = correctKeystrokes / durationMinutes / TestConstants.CHARACTERS_PER_WORD
    }
}