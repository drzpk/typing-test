package dev.drzepka.typing.server.presentation

import dev.drzepka.typing.server.domain.dto.ChangePasswordRequest
import dev.drzepka.typing.server.domain.dto.UpdateAccountSettingsRequest
import dev.drzepka.typing.server.domain.dto.UserAuthenticationDetailsDTO
import dev.drzepka.typing.server.domain.service.UserService
import dev.drzepka.typing.server.domain.util.getCurrentUser
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.get

fun Route.userController() {

    val userService = get<UserService>()

    route("/current-user") {
        get("/authentication-details") {
            val dto = transaction {
                val user = getCurrentUser()
                UserAuthenticationDetailsDTO.fromUserEntity(user)
            }
            call.respond(dto)
        }

        put("/update-settings") {
            val request = call.receive<UpdateAccountSettingsRequest>()

            transaction {
                val user = getCurrentUser()
                userService.updateSettings(user, request)
            }

            call.respond(HttpStatusCode.NoContent)
        }

        post("/change-password") {
            val request = call.receive<ChangePasswordRequest>()

            transaction {
                val user = getCurrentUser()
                userService.changePassword(user, request)
            }

            call.respond(HttpStatusCode.NoContent)
        }
    }
}
