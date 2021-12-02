package dev.drzepka.typing.server.presentation

import dev.drzepka.typing.server.application.dto.user.ChangePasswordRequest
import dev.drzepka.typing.server.application.dto.user.SearchUsersRequest
import dev.drzepka.typing.server.application.dto.user.UpdateAccountSettingsRequest
import dev.drzepka.typing.server.application.dto.user.UserAuthenticationDetailsDTO
import dev.drzepka.typing.server.application.security.adminInterceptor
import dev.drzepka.typing.server.application.service.UserService
import dev.drzepka.typing.server.application.service.UserSessionService
import dev.drzepka.typing.server.application.util.getCurrentRegisteredUser
import dev.drzepka.typing.server.application.util.getCurrentUser
import dev.drzepka.typing.server.application.util.getRequiredIntParam
import dev.drzepka.typing.server.domain.repository.UserRepository
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.get

fun Route.userController() {
    val userRepository = get<UserRepository>()
    val userService = get<UserService>()
    val userSessionService = get<UserSessionService>()

    route("/current-user") {
        get("/authentication-details") {
            val dto = transaction {
                val user = getCurrentUser(userRepository)
                UserAuthenticationDetailsDTO.fromUserEntity(user)
            }
            call.respond(dto)
        }

        put("/update-settings") {
            val request = call.receive<UpdateAccountSettingsRequest>()

            transaction {
                userSessionService.updateLastSeen(call)

                val user = getCurrentRegisteredUser(userRepository)
                userService.updateSettings(user, request)
            }

            call.respond(HttpStatusCode.NoContent)
        }

        post("/change-password") {
            val request = call.receive<ChangePasswordRequest>()

            transaction {
                userSessionService.updateLastSeen(call)

                val user = getCurrentRegisteredUser(userRepository)
                userService.changePassword(user, request)
            }

            call.respond(HttpStatusCode.NoContent)
        }

        delete("") {
            transaction {
                userSessionService.updateLastSeen(call)

                val user = getCurrentRegisteredUser(userRepository)
                userService.deleteUser(user.id!!)
            }

            call.respond(HttpStatusCode.NoContent)
        }
    }

    route("/users") {
        adminInterceptor()

        delete("/{userId}") {
            val userId = getRequiredIntParam("userId")

            transaction {
                userService.deleteUser(userId)
            }

            call.respond(HttpStatusCode.NoContent)
        }

        put("/{userId}/active") {
            val userId = getRequiredIntParam("userId")

            transaction {
                userService.activateUser(userId)
            }

            call.respond(HttpStatusCode.NoContent)
        }

        post("/search") {
            val request = call.receive<SearchUsersRequest>()

            val response = transaction {
                userService.searchUsers(request)
            }

            call.respond(response)
        }
    }
}
