package dev.drzepka.typing.server.presentation

import dev.drzepka.typing.server.domain.dto.wordlist.CreateWordListRequest
import dev.drzepka.typing.server.domain.dto.wordlist.WordListResource
import dev.drzepka.typing.server.domain.service.WordListService
import dev.drzepka.typing.server.domain.service.WordService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.get

fun Route.wordListController() {
    val wordListService = get<WordListService>()

    route("/word-lists") {
        get("") {
            val collection = transaction {
                wordListService.listWordLists().map { WordListResource.fromEntity(it) }
            }

            call.respond(collection)
        }

        post("") {
            val request = call.receive<CreateWordListRequest>()
            val resource = transaction {
                val created = wordListService.createWordList(request)
                WordListResource.fromEntity(created)
            }

            call.respond(HttpStatusCode.Created, resource)
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
                wordListService.deleteWordList(id)
            }

            call.respond(if (status) HttpStatusCode.NoContent else HttpStatusCode.NotFound)
        }

    }
}
