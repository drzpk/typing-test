package dev.drzepka.typing.server.domain.value

enum class TestState {
    /**
     * Test has been created and word list generated, but wasn't started yet.
     */
    CREATED,
    /**
     * Test has been created but wait timeout before start has been exceeded.
     */
    CREATED_TIMEOUT,
    /**
     * Test has been started.
     */
    STARTED,
    /**
     * Test has been started but wasn't finished within defined timeout.
     */
    STARTED_TIMEOUT,
    /**
     * Test has been finished
     */
    FINISHED
}