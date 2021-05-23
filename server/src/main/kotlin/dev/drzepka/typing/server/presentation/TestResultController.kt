package dev.drzepka.typing.server.presentation

import dev.drzepka.typing.server.application.service.TestResultService
import dev.drzepka.typing.server.application.util.getRequiredIntParam
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.get

fun Route.testResultController() {
    val testResultService = get<TestResultService>()

    route("/test-results") {
        get("/{testResultId}") {
            val testResultId = getRequiredIntParam("testResultId")

            val resource = transaction {
                testResultService.getResultForTest(testResultId)
            }

            call.respond(resource)
        }
    }
}