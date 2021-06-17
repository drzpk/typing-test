package dev.drzepka.typing.server.application.security

import java.security.SecureRandom

class PasswordGenerator {

    private val secureRandom = SecureRandom()

    fun generatePassword(minCharacters: Int, maxCharacters: Int, includeDigits: Boolean): String {
        val passwordLength = getRandomNumber(minCharacters, maxCharacters)

        val passwordArray = (0 until passwordLength).map { randomAlpha() }.toCharArray()
        if (includeDigits) {
            val digitCount = getRandomNumber(1, passwordLength - 1)
            repeat(digitCount) {
                val digitPos = getRandomNumber(0, passwordLength - 1)
                passwordArray[digitPos] = randomDigit()
            }
        }

        return passwordArray.concatToString()
    }

    private fun randomAlpha(): Char = ALPHA_CHARSET[getRandomNumber(0, ALPHA_CHARSET.length - 1)]

    private fun randomDigit(): Char = DIGIT_CHARSET[getRandomNumber(0, DIGIT_CHARSET.length - 1)]

    private fun getRandomNumber(start: Int, endInclusive: Int): Int =
        secureRandom.nextInt(endInclusive - start + 1) + start

    companion object {
        private const val ALPHA_CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        private const val DIGIT_CHARSET = "0123456789"
    }
}