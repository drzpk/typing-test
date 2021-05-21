package dev.drzepka.typing.server.application.exception

import io.ktor.http.*

class ErrorCodeException(
    val code: ErrorCode,
    override val message: String,
    val statusCode: HttpStatusCode,
    val `object`: Any? = null,
    val additionalData: Map<String, Any>? = null
) : RuntimeException()