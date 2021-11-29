package dev.drzepka.typing.server.infrastructure.service

import dev.drzepka.typing.server.domain.util.Mockable
import org.jetbrains.exposed.sql.Transaction

@Mockable
class TransactionService {

    fun <T> transaction(statement: Transaction.() -> T): T {
        return org.jetbrains.exposed.sql.transactions.transaction {
            statement.invoke(this)
        }
    }
}