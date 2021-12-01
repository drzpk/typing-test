package dev.drzepka.typing.server.infrastructure.util

import dev.drzepka.typing.server.domain.PagedQuery
import dev.drzepka.typing.server.domain.SearchQuery
import dev.drzepka.typing.server.domain.SortingQuery
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.*

fun IdTable<*>.countAllRows(): Long {
    val countExpression = id.count()
    val countQuery = slice(countExpression).selectAll()
    return countQuery.first()[countExpression]
}

fun IdTable<*>.countWhere(builder: SqlExpressionBuilder.() -> Op<Boolean>): Long {
    val countExpression = id.count()
    val countQuery = slice(countExpression).select(builder)
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

/**
 * A workaround for saving nullable foreign key
 */
class NullableForeignKeyWrapper<T : Comparable<T>>(private val value: T?) : Expression<EntityID<T>>() {
    override fun toQueryBuilder(queryBuilder: QueryBuilder) {
        queryBuilder.append(value?.toString() ?: "null")
    }
}