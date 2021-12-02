package dev.drzepka.typing.server.application.dto.user

import dev.drzepka.typing.server.domain.entity.User

data class UserAuthenticationDetailsDTO(
    val userId: Int,
    val email: String,
    val displayName: String,
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
                user.isAdmin(),
                false
            )
        }

        private fun anonymousUser(): UserAuthenticationDetailsDTO {
            return UserAuthenticationDetailsDTO(
                -1,
                "",
                "Anonymous",
                isAdmin = false,
                isAnonymous = true
            )
        }
    }
}