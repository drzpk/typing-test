package dev.drzepka.typing.server.presentation

import dev.drzepka.typing.server.domain.dto.word.AddWordRequest
import dev.drzepka.typing.server.domain.dto.word.WordResource
import dev.drzepka.typing.server.domain.service.WordService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.get

fun Route.wordController() {
    val wordService = get<WordService>()

    route("/words") {
        post("") {
            val request = call.receive<AddWordRequest>()
            val wordListId = call.parameters["wordListId"]!!.toInt()
            request.wordListId = wordListId

            val resource = transaction {
                val entity = wordService.addWord(request)
                WordResource.fromEntity(entity)
            }

            call.respond(HttpStatusCode.Created, resource)
        }

        get("") {
            val wordListId = call.parameters["wordListId"]!!.toInt()
            val list = transaction {
                wordService.listWords(wordListId).map { WordResource.fromEntity(it) }
            }

            call.respond(list)
        }

        delete("/{wordId}") {
            val wordId = call.parameters["wordId"]!!.toInt()
            val deleted = transaction {
                wordService.deleteWord(wordId)
            }

            call.respond(if (deleted) HttpStatusCode.NoContent else HttpStatusCode.NotFound)
        }
    }
}