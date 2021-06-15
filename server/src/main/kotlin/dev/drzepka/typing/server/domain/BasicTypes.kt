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

    companion object {
        fun <T : Any> empty(): Page<T> = Page(emptyList(), 1, 0, 0)
    }
}

interface PagedQuery {
    var page: Int
    var size: Int

    fun copyFrom(other: PagedQuery) {
        page = other.page
        size = other.size
    }
}

interface SortingQuery {
    var properties: List<Property>

    fun copyFrom(other: SortingQuery, columnNameMapping: Collection<Pair<String, String>>) {
        val mapped = ArrayList<Property>(other.properties.size)
        for (otherProp in other.properties) {
            val mappedName = columnNameMapping
                .firstOrNull { it.first == otherProp.property }

            val finalName = mappedName?.second ?: otherProp.property
            mapped.add(Property(finalName, otherProp.order))
        }

        this.properties = mapped
    }

    data class Property(var property: String, var order: Order = Order.ASC)

    enum class Order {
        ASC, DESC
    }
}

interface SearchQuery {
    var phrase: String?

    fun copyFrom(other: SearchQuery) {
        phrase = other.phrase
    }
}