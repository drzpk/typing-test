package dev.drzepka.typing.server.domain.entity

import dev.drzepka.typing.server.domain.value.TestState
import dev.drzepka.typing.server.domain.value.UserIdentity
import dev.drzepka.typing.server.domain.value.WordSelection
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import java.time.*

class TestTest {

    @Test
    fun `should pass through all states`() {
        var instant = getTime(1, 0, 0)
        val testDefinition = TestDefinition().apply {
            wordList = WordList()
        }

        val test =
            object : dev.drzepka.typing.server.domain.entity.Test(testDefinition, getUserIdentity(), WordSelection()) {
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
        test.testDefinition.duration = Duration.ofMinutes(2)
        test.startedAt = getTime(2, 1, 0)

        test.finishTimeLimit = test.testDefinition.duration!! + Duration.ofSeconds(15)

        instant = getTime(2, 3, 15)
        then(test.state).isEqualTo(TestState.STARTED)

        instant = getTime(2, 3, 16)
        then(test.state).isEqualTo(TestState.STARTED_TIMEOUT)


        // FINISHED
        test.createdAt = getTime(3, 0, 0)
        test.finishedAt = getTime(3, 2, 0)

        then(test.state).isEqualTo(TestState.FINISHED)
    }

    @Test
    fun `should get duration from test definition with specified duration`() {
        val definition = TestDefinition().apply {
            duration = Duration.ofMinutes(2)
        }
        val test = Test(definition, getUserIdentity(), WordSelection())

        then(test.duration).isEqualTo(Duration.ofMinutes(2))
    }

    @Test
    fun `should get duration directly from test with definition without specified duration`() {
        val testDefinition = TestDefinition().apply {
            wordList = WordList().apply { fixedTextType("test text") }
            duration = null
        }

        val test = Test(testDefinition, getUserIdentity(), WordSelection()).apply {
            val now = Instant.now()
            startedAt = now
            finishedAt = now.plusSeconds(72)
        }

        then(test.duration).isEqualTo(Duration.ofSeconds(72))
    }

    private fun getUserIdentity(): UserIdentity = UserIdentity(User(), 1)

    private fun getTime(h: Int, m: Int, s: Int): Instant {
        return LocalDate.now()
            .atTime(LocalTime.of(h, m, s))
            .atZone(ZoneId.systemDefault())
            .toInstant()
    }
}