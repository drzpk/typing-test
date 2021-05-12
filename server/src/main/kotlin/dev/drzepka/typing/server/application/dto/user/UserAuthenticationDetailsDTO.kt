package dev.drzepka.typing.server.application.dto.user

import dev.drzepka.typing.server.domain.entity.User

data class UserAuthenticationDetailsDTO(
    val userId: Int,
    val email: String,
    val displayName: String,
    val isAdmin: Boolean
) {
    companion object {
        fun fromUserEntity(user: User): UserAuthenticationDetailsDTO {
            return UserAuthenticationDetailsDTO(
                user.id!!,
                user.email,
                user.displayName,
                user.isAdmin()
            )
        }
    }
}