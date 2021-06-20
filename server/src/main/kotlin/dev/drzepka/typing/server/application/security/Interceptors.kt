package dev.drzepka.typing.server.application.security

import dev.drzepka.typing.server.application.TypingTestSession
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.util.pipeline.*
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("Interceptors")

fun Route.sessionInterceptor() {
    intercept(ApplicationCallPipeline.Features) {
        val session = call.sessions.get<TypingTestSession>()
        if (session == null) {
            call.respond(HttpStatusCode.Unauthorized)
            finish()
        }
    }
}

fun Route.adminInterceptor() {
    intercept(ApplicationCallPipeline.Features) {
        adminVerifier()
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.adminVerifier() {
    val session = call.sessions.get<TypingTestSession>()!!
    if (!session.isAdmin) {
        log.warn("Non-admin user attempted to access admin-only resource at " + call.request.uri)
        call.respond(HttpStatusCode.Forbidden)
        finish()
    }
}