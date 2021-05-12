package dev.drzepka.typing.server.presentation

import dev.drzepka.typing.server.application.dto.testdefinition.CreateTestDefinitionRequest
import dev.drzepka.typing.server.application.dto.testdefinition.UpdateTestDefinitionRequest
import dev.drzepka.typing.server.application.service.TestDefinitionService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.get

fun Route.testDefinitionController() {
    val testDefinitionService = get<TestDefinitionService>()

    route("/test-definitions") {
        get("") {
            val rawActive = call.parameters["active"]
            val active = if (rawActive != null) rawActive == "true" else null
            val collection = transaction {
                testDefinitionService.listTestDefinitions(active)
            }

            call.respond(collection)
        }

        post("") {
            val request = call.receive<CreateTestDefinitionRequest>()
            val resource = transaction {
                testDefinitionService.createTestDefinition(request)
            }

            call.respond(HttpStatusCode.Created, resource)
        }

        patch("/{testDefinitionId}") {
            val testDefinitionId = call.parameters["testDefinitionId"]!!.toInt()
            val request = call.receive<UpdateTestDefinitionRequest>()
            request.id = testDefinitionId

            val resource = transaction {
                testDefinitionService.updateTestDefinition(request)
            }

            if (resource != null)
                call.respond(HttpStatusCode.Created, resource)
            else
                call.respond(HttpStatusCode.NotFound)
        }
    }
}
