package dev.drzepka.typing.server.presentation

import dev.drzepka.typing.server.application.dto.test.CreateTestRequest
import dev.drzepka.typing.server.application.dto.test.FinishTestRequest
import dev.drzepka.typing.server.application.service.TestManagerService
import dev.drzepka.typing.server.application.service.UserSessionService
import dev.drzepka.typing.server.application.util.getCurrentIdentity
import dev.drzepka.typing.server.application.util.getRequiredIntParam
import dev.drzepka.typing.server.domain.repository.UserRepository
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
    val userSessionService = get<UserSessionService>()

    route("/tests") {
        post("") {
            val request = call.receive<CreateTestRequest>()

            val test = transaction {
                userSessionService.updateLastSeen(call)
                val identity = getCurrentIdentity(userRepository)
                testManagerService.createTest(request, identity)
            }

            call.respond(HttpStatusCode.Created, test)
        }

        get("/{testId}") {
            val testId = getRequiredIntParam("testId")

            val resource = transaction {
                val identity = getCurrentIdentity(userRepository)
                testManagerService.getTest(testId, identity)
            }

            call.respond(resource)
        }

        delete("/{testId}") {
            val testId = getRequiredIntParam("testId")
            transaction {
                userSessionService.updateLastSeen(call)
                val identity = getCurrentIdentity(userRepository)
                testManagerService.deleteTest(testId, identity)
            }

            call.respond(HttpStatusCode.NoContent)
        }

        post("/{testId}/words") {
            val testId = getRequiredIntParam("testId")

            val resource = transaction {
                userSessionService.updateLastSeen(call)
                val identity = getCurrentIdentity(userRepository)
                testManagerService.regenerateWordList(testId, identity)
            }

            call.respond(resource)
        }

        post("/{testId}/start") {
            val testId = getRequiredIntParam("testId")
            val resource = transaction {
                val identity = getCurrentIdentity(userRepository)

                testManagerService.startTest(testId, identity)
            }

            call.respond(resource)
        }

        post("/{testId}/finish") {
            val testId = getRequiredIntParam("testId")
            val request = call.receive<FinishTestRequest>()

            val resource = transaction {
                val identity = getCurrentIdentity(userRepository)
                testManagerService.finishTest(testId, request, identity)
            }

            call.respond(resource)
        }

        get("/{testId}/result") {
            val testId = getRequiredIntParam("testId")
            call.respondRedirect("/api/test-results/$testId")
        }
    }
}
