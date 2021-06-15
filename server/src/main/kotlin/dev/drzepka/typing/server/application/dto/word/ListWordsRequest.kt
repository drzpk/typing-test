package dev.drzepka.typing.server.application.dto.word

import dev.drzepka.typing.server.application.dto.PagedRequest

class ListWordsRequest : PagedRequest() {
    var wordListId = 0
}