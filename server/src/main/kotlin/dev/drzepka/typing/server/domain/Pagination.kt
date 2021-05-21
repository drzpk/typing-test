package dev.drzepka.typing.server.domain

import kotlin.math.ceil

data class Page<T : Any>(
    val content: Collection<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long
) {
    constructor(content: Collection<T>, pagedQuery: PagedQuery, totalElements: Long) : this(
        content,
        pagedQuery.page,
        pagedQuery.size,
        totalElements
    )

    val totalPages: Int
        get() = ceil(totalElements.toDouble() / size).toInt()
}

interface PagedQuery {
    var page: Int
    var size: Int
}

open class PagedRequest : PagedQuery {
    override var page = 1
    override var size = 10
}

@Suppress("unused")
class PagedResourceCollection<T>(val content: Collection<T>, val metadata: PageMetadata) {

    companion object {
        fun <T : Any, U : Any> fromPage(source: Page<T>, mapper: (source: T) -> U): PagedResourceCollection<U> {
            val mapped = source.content.map { mapper.invoke(it) }
            val metadata = PageMetadata(source.page, source.size, source.totalElements, source.totalPages)
            return PagedResourceCollection(mapped, metadata)
        }
    }

    data class PageMetadata(val page: Int, val size: Int, val totalElements: Long, val totalPages: Int)
}