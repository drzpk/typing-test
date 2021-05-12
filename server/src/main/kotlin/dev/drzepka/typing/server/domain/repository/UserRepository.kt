package dev.drzepka.typing.server.domain.repository

import dev.drzepka.typing.server.domain.entity.User

interface UserRepository {
    fun findById(id: Int): User?
    fun findByEmail(email: String): User?
    fun save(user: User)
    fun delete(userId: Int): Boolean
}