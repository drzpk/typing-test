package dev.drzepka.typing.server.presentation

import dev.drzepka.typing.server.application.dto.BulkImportWordsDTO
import dev.drzepka.typing.server.application.dto.ErrorHandlingMode
import dev.drzepka.typing.server.application.dto.PagedResourceCollection
import dev.drzepka.typing.server.application.dto.word.AddWordRequest
import dev.drzepka.typing.server.application.dto.word.ListWordsRequest
import dev.drzepka.typing.server.application.dto.word.PatchWordRequest
import dev.drzepka.typing.server.application.dto.word.WordResource
import dev.drzepka.typing.server.application.service.WordBulkManagementService
import dev.drzepka.typing.server.application.service.WordService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.get
import java.nio.charset.StandardCharsets

fun Route.wordController() {
    val wordService = get<WordService>()
    val wordBulkManagementService = get<WordBulkManagementService>()

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

        post("/bulk-import") {
            val multipart = call.receiveMultipart()
            val partData = multipart.readPart()!! as PartData.FileItem
            val content = partData.streamProvider().readBytes().toString(StandardCharsets.UTF_8)

            val dto = BulkImportWordsDTO(
                call.parameters["wordListId"]!!.toInt(),
                content,
                if (call.parameters["ignoreErrors"] != null) ErrorHandlingMode.IGNORE else ErrorHandlingMode.ABORT
            )

            transaction {
                wordBulkManagementService.bulkImportWords(dto)
            }

            call.respond(HttpStatusCode.Created)
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
                wordService.deleteWord(wordId)
            }

            call.respond(if (deleted) HttpStatusCode.NoContent else HttpStatusCode.NotFound)
        }
    }
}