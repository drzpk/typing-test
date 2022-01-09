package dev.drzepka.typing.server.domain.entity

import org.assertj.core.api.BDDAssertions.then
import org.assertj.core.data.Offset
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneId

internal class UserTest {

    @Test
    fun `should calculate tests per day`() {
        val completedTests = 388
        val from = LocalDateTime.of(2021, 9, 1, 14, 1)
        val until = LocalDateTime.of(2021, 9, 11, 14, 0)

        val user = User().apply {
            createdAt = from.atZone(ZoneId.systemDefault()).toInstant()
        }

        val testsPerDay = user.getTestsPerDay(completedTests, until.atZone(ZoneId.systemDefault()).toInstant())
        // 0.0673611
        then(testsPerDay).isCloseTo(38.96f, Offset.strictOffset(0.01f))
    }
}