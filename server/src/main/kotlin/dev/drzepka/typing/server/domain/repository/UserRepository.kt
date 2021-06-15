package dev.drzepka.typing.server.domain.repository

import dev.drzepka.typing.server.domain.Page
import dev.drzepka.typing.server.domain.entity.User
import dev.drzepka.typing.server.domain.value.UserSearchQuery

interface UserRepository {
    fun findById(id: Int): User?
    fun findByEmail(email: String): User?
    fun search(query: UserSearchQuery): Page<User>
    fun save(user: User)
    fun delete(userId: Int): Boolean
}