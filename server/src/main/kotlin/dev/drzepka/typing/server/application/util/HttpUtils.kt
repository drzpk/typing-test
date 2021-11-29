package dev.drzepka.typing.server.application.util

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.request.*

object HttpUtils {
    fun getRemoteIp(call: ApplicationCall): String {
        return call.request.origin.remoteHost
    }

    fun getUserAgent(call: ApplicationCall): String {
        return call.request.userAgent() ?: ""
    }
}