package dev.drzepka.typing.server.application.dto.user

import dev.drzepka.typing.server.domain.PagedQuery
import dev.drzepka.typing.server.domain.SearchQuery
import dev.drzepka.typing.server.domain.SortingQuery

class SearchUsersRequest : PagedQuery, SortingQuery, SearchQuery {
    override var page = 1
    override var size = 10
    override var properties: List<SortingQuery.Property> = emptyList()
    override var phrase: String? = null

    var inactiveOnly = false
}