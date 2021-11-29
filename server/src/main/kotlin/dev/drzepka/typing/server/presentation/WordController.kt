package dev.drzepka.typing.server.presentation

import dev.drzepka.typing.server.application.dto.PagedResourceCollection
import dev.drzepka.typing.server.application.dto.word.*
import dev.drzepka.typing.server.application.service.UserSessionService
import dev.drzepka.typing.server.application.service.WordExportService
import dev.drzepka.typing.server.application.service.WordImportService
import dev.drzepka.typing.server.application.service.WordService
import dev.drzepka.typing.server.application.util.getRequiredIntParam
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.get

fun Route.wordController() {
    val wordService = get<WordService>()
    val wordImportService = get<WordImportService>()
    val wordExportService = get<WordExportService>()
    val userSessionService = get<UserSessionService>()

    route("/words") {
        post("") {
            val request = call.receive<AddWordRequest>()
            val wordListId = call.parameters["wordListId"]!!.toInt()
            request.wordListId = wordListId

            val resource = transaction {
                userSessionService.updateLastSeen(call)
                val entity = wordService.addWord(request)
                WordResource.fromEntity(entity)
            }

            call.respond(HttpStatusCode.Created, resource)
        }

        get("") {
            val request = ListWordsRequest().apply {
                wordListId = call.parameters["wordListId"]!!.toInt()
                if (call.parameters["page"] != null)
                    page = call.parameters["page"]!!.toInt()
                if (call.parameters["size"] != null)
                    size = call.parameters["size"]!!.toInt()
            }

            val collection = transaction {
                val page = wordService.listWords(request)
                PagedResourceCollection.fromPage(page) { WordResource.fromEntity(it) }
            }

            call.respond(collection)
        }

        patch("/{wordId}") {
            val request = call.receive<PatchWordRequest>()
            request.wordId = call.parameters["wordId"]!!.toInt()

            val updated = transaction {
                userSessionService.updateLastSeen(call)
                wordService.updateWord(request)
            }

            if (updated)
                call.respond(HttpStatusCode.NoContent)
            else
                call.respond(HttpStatusCode.NotFound)
        }

        delete("/{wordId}") {
            val wordId = call.parameters["wordId"]!!.toInt()
            val deleted = transaction {
                userSessionService.updateLastSeen(call)
                wordService.deleteWord(wordId)
            }

            call.respond(if (deleted) HttpStatusCode.NoContent else HttpStatusCode.NotFound)
        }

        post("/import") {
            val wordListId = getRequiredIntParam("wordListId")
            val request = call.receive<ImportWordsRequest>()
            request.wordListId = wordListId

            transaction {
                userSessionService.updateLastSeen(call)
                wordImportService.importWords(request)
            }

            call.respond(HttpStatusCode.NoContent)
        }

        get("/export") {
            val wordListId = getRequiredIntParam("wordListId")

            newSuspendedTransaction {
                call.respondOutputStream(ContentType.Application.Json) {
                    wordExportService.exportWordList(wordListId, this)
                }
            }
        }
    }
}