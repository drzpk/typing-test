package dev.drzepka.typing.server.domain.dto.word

import dev.drzepka.typing.server.domain.PagedRequest

class ListWordsRequest : PagedRequest() {
    var wordListId = 0
}