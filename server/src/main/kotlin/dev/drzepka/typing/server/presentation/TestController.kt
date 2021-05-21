package dev.drzepka.typing.server.presentation

import dev.drzepka.typing.server.application.dto.test.CreateTestRequest
import dev.drzepka.typing.server.application.dto.test.FinishTestRequest
import dev.drzepka.typing.server.application.service.TestManagerService
import dev.drzepka.typing.server.application.util.getRequiredIntParam
import dev.drzepka.typing.server.domain.repository.UserRepository
import dev.drzepka.typing.server.domain.util.getCurrentUser
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.get

fun Route.testController() {
    val userRepository = get<UserRepository>()
    val testManagerService = get<TestManagerService>()

    route("/tests") {
        post("") {
            val request = call.receive<CreateTestRequest>()

            val test = transaction {
                val user = getCurrentUser(userRepository)
                testManagerService.createTest(request, user)
            }

            call.respond(HttpStatusCode.Created, test)
        }

        get("/{testId}") {
            val testId = getRequiredIntParam("testId")

            val resource = transaction {
                testManagerService.getTest(testId)
            }

            call.respond(resource)
        }

        delete("/{testId}") {
            val testId = getRequiredIntParam("testId")
            transaction {
                testManagerService.deleteTest(testId)
            }

            call.respond(HttpStatusCode.NoContent)
        }

        post("/{testId}/words") {
            val testId = getRequiredIntParam("testId")
            val resource = transaction {
                testManagerService.regenerateWordList(testId)
            }

            call.respond(resource)
        }

        post("/{testId}/start") {
            val testId = getRequiredIntParam("testId")
            val resource = transaction {
                testManagerService.startTest(testId)
            }

            call.respond(resource)
        }

        post("/{testId}/finish") {
            val testId = getRequiredIntParam("testId")
            val request = call.receive<FinishTestRequest>()

            val resource = transaction {
                testManagerService.finishTest(testId, request)
            }

            call.respond(resource)
        }
    }
}
