package dev.drzepka.typing.server.presentation

import dev.drzepka.typing.server.application.dto.testdefinition.CreateTestDefinitionRequest
import dev.drzepka.typing.server.application.dto.testdefinition.UpdateTestDefinitionRequest
import dev.drzepka.typing.server.application.dto.testresult.TestResultRange
import dev.drzepka.typing.server.application.security.adminVerifier
import dev.drzepka.typing.server.application.service.TestDefinitionService
import dev.drzepka.typing.server.application.service.TestResultService
import dev.drzepka.typing.server.application.service.UserSessionService
import dev.drzepka.typing.server.application.util.getRequiredIntParam
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.get

fun Route.testDefinitionController() {
    val testDefinitionService = get<TestDefinitionService>()
    val testResultService = get<TestResultService>()
    val userSessionService = get<UserSessionService>()

    route("/test-definitions") {
        get("") {
            val rawActive = call.parameters["active"]
            val active = if (rawActive != null) rawActive == "true" else null

            val collection = transaction {
                userSessionService.updateLastSeen(call)
                testDefinitionService.listTestDefinitions(active)
            }

            call.respond(collection)
        }

        get("{testDefinitionId}") {
            val testDefinitionId = getRequiredIntParam("testDefinitionId")

            val resource = transaction {
                testDefinitionService.getTestDefinition(testDefinitionId)
            }

            if (resource != null)
                call.respond(resource)
            else
                call.respond(HttpStatusCode.NotFound)
        }

        get("/{testDefinitionId}/best-results") {
            val testDefinitionId = getRequiredIntParam("testDefinitionId")
            val range = call.parameters["range"]?.let { TestResultRange.valueOf(it) }

            val resources = transaction {
                testResultService.getBestResults(testDefinitionId, range)
            }

            call.respond(resources)
        }


        post("") {
            adminVerifier()

            val request = call.receive<CreateTestDefinitionRequest>()

            val resource = transaction {
                userSessionService.updateLastSeen(call)
                testDefinitionService.createTestDefinition(request)
            }

            call.respond(HttpStatusCode.Created, resource)
        }

        patch("/{testDefinitionId}") {
            adminVerifier()

            val testDefinitionId = call.parameters["testDefinitionId"]!!.toInt()
            val request = call.receive<UpdateTestDefinitionRequest>()
            request.id = testDefinitionId

            val resource = transaction {
                userSessionService.updateLastSeen(call)
                testDefinitionService.updateTestDefinition(request)
            }

            if (resource != null)
                call.respond(resource)
            else
                call.respond(HttpStatusCode.NotFound)
        }

        delete("/{testDefinitionId}") {
            adminVerifier()

            val testDefinitionId = getRequiredIntParam("testDefinitionId")
            val status = transaction {
                userSessionService.updateLastSeen(call)
                testDefinitionService.deleteTestDefinition(testDefinitionId)
            }

            call.respond(if (status) HttpStatusCode.NoContent else HttpStatusCode.NotFound)
        }
    }
}
