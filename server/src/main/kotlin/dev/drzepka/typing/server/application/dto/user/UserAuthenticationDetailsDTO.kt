package dev.drzepka.typing.server.application.dto.user

import dev.drzepka.typing.server.domain.entity.User
import java.time.Instant

data class UserAuthenticationDetailsDTO(
    val userId: Int,
    val email: String,
    val displayName: String,
    val createdAt: Instant,
    val isAdmin: Boolean,
    val isAnonymous: Boolean
) {
    companion object {
        fun fromUserEntity(user: User?): UserAuthenticationDetailsDTO {
            if (user == null)
                return anonymousUser()

            return UserAuthenticationDetailsDTO(
                user.id!!,
                user.email,
                user.displayName,
                user.createdAt,
                user.isAdmin(),
                false
            )
        }

        private fun anonymousUser(): UserAuthenticationDetailsDTO {
            return UserAuthenticationDetailsDTO(
                -1,
                "",
                "Anonymous",
                Instant.now(),
                isAdmin = false,
                isAnonymous = true
            )
        }
    }
}