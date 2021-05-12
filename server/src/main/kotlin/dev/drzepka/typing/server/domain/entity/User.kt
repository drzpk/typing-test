package dev.drzepka.typing.server.domain.entity

import dev.drzepka.typing.server.domain.util.Logger
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class User : AbstractEntity<Int>() {

    var email = ""
    var displayName = ""
    var password = ""
    var createdAt: Instant = Instant.now()
    var activatedAt: Instant? = null

    fun isAdmin(): Boolean = email == ADMIN_USER_EMAIL

    fun activate() {
        if (activatedAt != null)
            return

        log.info("Activating user {}", email)
        transaction {
            activatedAt = Instant.now()
        }
    }

    companion object  {
        const val ADMIN_USER_EMAIL = "admin@drzepka.dev"
        private val log by Logger()
    }
}