package dev.drzepka.typing.server.domain.entity

import dev.drzepka.typing.server.domain.value.TestState
import dev.drzepka.typing.server.domain.value.WordSelection
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import java.time.*

class TestTest {

    @Test
    fun `should pass through all states`() {
        var instant = getTime(1, 0, 0)
        val test = object : dev.drzepka.typing.server.domain.entity.Test(TestDefinition(), User(), WordSelection()) {
            override fun now(): Instant {
                return instant
            }
        }

        // CREATED
        then(test.state).isEqualTo(TestState.CREATED)


        // CREATED_TIMEOUT
        test.createdAt = getTime(1, 0, 0)
        test.startTimeLimit = Duration.ofSeconds(30)

        instant = getTime(1, 0, 30)
        then(test.state).isEqualTo(TestState.CREATED)

        instant = getTime(1, 0, 31)
        then(test.state).isEqualTo(TestState.CREATED_TIMEOUT)


        // STARTED
        test.createdAt = getTime(1, 0, 0)
        test.startTimeLimit = Duration.ofMinutes(5)
        test.startedAt = getTime(1, 1, 0)

        instant = getTime(1, 2, 0)
        then(test.state).isEqualTo(TestState.STARTED)


        // STARTED_TIMEOUT
        test.createdAt = getTime(2, 0, 0)
        test.startTimeLimit = Duration.ofMinutes(5)
        test.startedAt = getTime(2, 1, 0)

        test.finishTimeLimit = Duration.ofSeconds(15)

        instant = getTime(2, 1, 15)
        then(test.state).isEqualTo(TestState.STARTED)

        instant = getTime(2, 1, 16)
        then(test.state).isEqualTo(TestState.STARTED_TIMEOUT)


        // FINISHED
        test.createdAt = getTime(3, 0, 0)
        test.finishedAt = getTime(3, 2, 0)

        then(test.state).isEqualTo(TestState.FINISHED)
    }

    private fun getTime(h: Int, m: Int, s: Int): Instant {
        return LocalDate.now()
            .atTime(LocalTime.of(h, m, s))
            .atZone(ZoneId.systemDefault())
            .toInstant()
    }
}