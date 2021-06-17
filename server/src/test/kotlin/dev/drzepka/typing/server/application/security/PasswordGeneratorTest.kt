package dev.drzepka.typing.server.application.security

import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import java.util.regex.Pattern

class PasswordGeneratorTest {

    @Test
    fun `should generate password without digits`() {
        val pattern = Pattern.compile("[a-zA-Z]+")
        val generator = PasswordGenerator()

        repeat(100) {
            val generated = generator.generatePassword(10, 20, false)
            then(generated)
                .hasSizeBetween(10, 20)
                .matches(pattern)
        }
    }

    @Test
    fun `should generate password with digits`() {
        val pattern = Pattern.compile("[a-zA-Z0-9]+")
        val service = PasswordGenerator()

        repeat(100) {
            val generated = service.generatePassword(10, 20, true)
            then(generated)
                .hasSizeBetween(10, 20)
                .matches(pattern)
                .matches { password ->
                    password.findAnyOf(IntRange(0, 9).map { it.toString() }) != null
                }
        }
    }

}