package dev.drzepka.typing.server.domain.dto.user

import dev.drzepka.typing.server.domain.entity.User

data class UserAuthenticationDetailsDTO(
    val userId: Int,
    val email: String,
    val displayName: String
) {
    companion object {
        fun fromUserEntity(user: User): UserAuthenticationDetailsDTO {
            return UserAuthenticationDetailsDTO(
                user.id.value,
                user.email,
                user.displayName
            )
        }
    }
}