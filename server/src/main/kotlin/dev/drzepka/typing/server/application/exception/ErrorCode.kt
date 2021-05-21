package dev.drzepka.typing.server.application.exception

import io.ktor.http.*

enum class ErrorCode(private val message: String, private val statusCode: HttpStatusCode = HttpStatusCode.BadRequest) {
    TEST_DEFINITION_NOT_FOUND("Test definition wasn't found.", HttpStatusCode.NotFound),
    CANNOT_DELETE_FINISHED_TEST("Finished test cannot be deleted."),
    TEST_START_TIMEOUT("Time for starting the test has expired."),
    TEST_START_WRONG_STATE("Cannot start test because of wrong state."),
    TEST_FINISH_TIMEOUT("Time for finishing the test has expired."),
    TEST_FINISH_WRONG_STATE("Cannot finish test because of wrong state."),
    TEST_REGENERATE_WORD_ERROR("Cannot regenerate words because of wrong state.");

    fun throwError(principal: Any? = null, additionalData: Map<String, Any>? = null): Nothing {
        throw ErrorCodeException(this, message, statusCode, principal, additionalData)
    }
}