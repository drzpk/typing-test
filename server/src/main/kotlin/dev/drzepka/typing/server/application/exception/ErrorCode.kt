package dev.drzepka.typing.server.application.exception

import io.ktor.http.*

enum class ErrorCode(
    private val defaultMessage: String,
    private val statusCode: HttpStatusCode = HttpStatusCode.BadRequest
) {
    REQUEST_SYNTAX_INCORRECT(
        "The request syntax is incorrect. Ensure that request was properly formed.",
        HttpStatusCode.BadRequest
    ),
    TEST_DEFINITION_NOT_FOUND("Test definition wasn't found.", HttpStatusCode.NotFound),
    CANNOT_DELETE_FINISHED_TEST("Finished test cannot be deleted."),
    TEST_START_TIMEOUT("Time for starting the test has expired."),
    TEST_START_WRONG_STATE("Cannot start test because of wrong state."),
    TEST_FINISH_TIMEOUT("Time for finishing the test has expired."),
    TEST_FINISH_WRONG_STATE("Cannot finish test because of wrong state."),
    TEST_REGENERATE_WORD_ERROR("Cannot regenerate words because of wrong state."),

    FIXED_TEXT_TOO_SHORT("Fixed text is too short", HttpStatusCode.BadRequest),
    TEST_RESULT_NOT_FOUND("Test result wasn't found", HttpStatusCode.NotFound),

    USER_NOT_FOUND("User wasn't found", HttpStatusCode.NotFound),
    CANNOT_DELETE_ADMIN_USER("Admin user can't be deleted.", HttpStatusCode.BadRequest),

    WORD_LIST_USED_BY_TEST_DEFINITIONS(
        "Word list is used by one or more test definitions.",
        HttpStatusCode.UnprocessableEntity
    );

    fun throwException(
        principal: Any? = null,
        additionalData: Map<String, Any>? = null,
        message: String? = null
    ): Nothing = throw getException(principal, additionalData, message)

    fun getException(
        principal: Any? = null,
        additionalData: Map<String, Any>? = null,
        message: String? = null
    ): ErrorCodeException {
        val finalMessage = message ?: defaultMessage
        return ErrorCodeException(this, finalMessage, statusCode, principal, additionalData)
    }
}