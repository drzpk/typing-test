package dev.drzepka.typing.server.presentation

import dev.drzepka.typing.server.application.service.TestResultService
import dev.drzepka.typing.server.application.service.TestStatsService
import dev.drzepka.typing.server.application.util.getCurrentRegisteredUser
import dev.drzepka.typing.server.application.util.getRequiredIntParam
import dev.drzepka.typing.server.domain.repository.UserRepository
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.get

fun Route.testResultController() {
    val testResultService = get<TestResultService>()
    val testStatsService = get<TestStatsService>()
    val userRepository = get<UserRepository>()

    route("/test-results") {
        get("/{testResultId}") {
            val testResultId = getRequiredIntParam("testResultId")

            val resource = transaction {
                testResultService.getResultForTest(testResultId)
            }

            call.respond(resource)
        }

        route("/stats") {
            get("/test-definitions") {
                val resource = transaction {
                    val user = getCurrentRegisteredUser(userRepository)
                    testStatsService.getTestDefinitionsWithStats(user)
                }

                call.respond(resource)
            }

            get("/test-definitions/{definitionId}") {
                val definitionId = getRequiredIntParam("definitionId")

                val resource = transaction {
                    val user = getCurrentRegisteredUser(userRepository)
                    testStatsService.calculateTestStats(user, definitionId)
                }

                call.respond(resource)
            }
        }
    }
}