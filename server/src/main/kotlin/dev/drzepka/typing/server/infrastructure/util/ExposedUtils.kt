package dev.drzepka.typing.server.infrastructure.util

import dev.drzepka.typing.server.infrastructure.repository.table.Words
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.selectAll

fun IdTable<*>.countAllRows(): Long {
    val countExpression = this.id.count()
    val countQuery = Words.slice(countExpression).selectAll()
    return countQuery.first()[countExpression]
}