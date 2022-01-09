package dev.drzepka.typing.server.domain.entity

import dev.drzepka.typing.server.domain.util.Logger
import java.time.Duration
import java.time.Instant

class User : AbstractEntity<Int>() {

    var email = ""
    var displayName = ""
    var password = ""
    var createdAt: Instant = Instant.now()
    var activatedAt: Instant? = null

    fun isAdmin(): Boolean = email == ADMIN_USER_EMAIL

    fun isActive(): Boolean = activatedAt != null

    fun activate() {
        if (activatedAt != null)
            return

        log.info("Activating user {}", email)
        activatedAt = Instant.now()
    }

    fun getTestsPerDay(totalTests: Int, until: Instant = Instant.now()): Float {
        val fullHours = Duration.between(createdAt, until).toHours().toFloat()
        return totalTests / (fullHours / 24)
    }

    companion object {
        const val ADMIN_USER_EMAIL = "admin@drzepka.dev"
        private val log by Logger()
    }
}