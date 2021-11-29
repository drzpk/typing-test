package dev.drzepka.typing.server.presentation

import dev.drzepka.typing.server.application.dto.wordlist.CreateWordListRequest
import dev.drzepka.typing.server.application.dto.wordlist.UpdateWordListRequest
import dev.drzepka.typing.server.application.dto.wordlist.WordListResource
import dev.drzepka.typing.server.application.security.adminInterceptor
import dev.drzepka.typing.server.application.service.UserSessionService
import dev.drzepka.typing.server.application.service.WordListService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.get

fun Route.wordListController() {
    val wordListService = get<WordListService>()
    val userSessionService = get<UserSessionService>()

    route("/word-lists") {
        adminInterceptor()

        get("") {
            val collection = transaction {
                wordListService.listWordLists().map { WordListResource.fromEntity(it) }
            }

            call.respond(collection)
        }

        post("") {
            val request = call.receive<CreateWordListRequest>()

            val resource = transaction {
                userSessionService.updateLastSeen(call)

                val created = wordListService.createWordList(request)
                WordListResource.fromEntity(created)
            }

            call.respond(HttpStatusCode.Created, resource)
        }

        patch("/{id}") {
            val request = call.receive<UpdateWordListRequest>()
            request.id = call.parameters["id"]!!.toInt()

            val resource = transaction {
                userSessionService.updateLastSeen(call)

                val updated = wordListService.updateWordList(request)
                WordListResource.fromEntity(updated)
            }

            call.respond(resource)
        }

        route("/{wordListId}") {
           get("") {
               val id = call.parameters["wordListId"]!!.toInt()
               val resource = transaction {
                   wordListService.getWordList(id)?.let { WordListResource.fromEntity(it) }
               }

               if (resource != null)
                   call.respond(resource)
               else
                   call.respond(HttpStatusCode.NotFound)
           }

            wordController()
        }

        delete("/{id}") {
            val id = call.parameters["id"]!!.toInt()

            val status = transaction {
                userSessionService.updateLastSeen(call)
                wordListService.deleteWordList(id)
            }

            call.respond(if (status) HttpStatusCode.NoContent else HttpStatusCode.NotFound)
        }

    }
}
