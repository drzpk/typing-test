package dev.drzepka.typing.server.domain.value

import dev.drzepka.typing.server.domain.entity.User

data class UserIdentity(val user: User, val sessionId: Int?) {
    override fun toString(): String = "UserIdentity(user=${user.id}, sessionId=$sessionId)"
}