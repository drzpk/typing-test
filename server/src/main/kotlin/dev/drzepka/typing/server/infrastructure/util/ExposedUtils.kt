package dev.drzepka.typing.server.infrastructure.util

import dev.drzepka.typing.server.domain.PagedQuery
import dev.drzepka.typing.server.domain.SearchQuery
import dev.drzepka.typing.server.domain.SortingQuery
import dev.drzepka.typing.server.infrastructure.repository.table.Words
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.*

fun IdTable<*>.countAllRows(): Long {
    val countExpression = this.id.count()
    val countQuery = Words.slice(countExpression).selectAll()
    return countQuery.first()[countExpression]
}

fun Query.paged(pagedQuery: PagedQuery): Query {
    this.limit(pagedQuery.size, (pagedQuery.page - 1) * pagedQuery.size.toLong())
    return this
}

fun Query.sorted(sortingQuery: SortingQuery): Query {
    val orders = ArrayList<Pair<Expression<*>, SortOrder>>()

    for (prop in sortingQuery.properties) {
        val column = this.set.source.columns
            .firstOrNull { it.name == prop.property }
            ?: throw IllegalArgumentException("Column ${prop.property} wasn't found.")
        val order = if (prop.order == SortingQuery.Order.ASC) SortOrder.ASC else SortOrder.DESC

        orders.add(Pair(column, order))
    }

    if (orders.isNotEmpty())
        this.orderBy(*orders.toTypedArray())

    return this
}

fun Query.filtered(column: Column<String>, searchQuery: SearchQuery): Query {
    if (searchQuery.phrase != null)
        this.andWhere { column like "%${searchQuery.phrase}%" }

    return this
}