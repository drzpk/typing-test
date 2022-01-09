package dev.drzepka.typing.server.application.dto.user

import dev.drzepka.typing.server.domain.entity.User
import java.time.Instant

@Suppress("unused")
class UserResource(
    var id: Int,
    var email: String,
    var displayName: String,
    var createdAt: Instant,
    var activatedAt: Instant? = null,
    var isAdmin: Boolean
) {
    val isActive: Boolean
        get() = activatedAt != null

    companion object {
        fun fromEntity(entity: User): UserResource {
            return UserResource(
                entity.id!!,
                entity.email,
                entity.displayName,
                entity.createdAt,
                entity.activatedAt,
                entity.isAdmin()
            )
        }
    }
}