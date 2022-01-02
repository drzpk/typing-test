package dev.drzepka.typing.server.domain.repository

import dev.drzepka.typing.server.domain.entity.Session
import java.time.Instant

interface SessionRepository {
    fun save(session: Session)
    fun updateLastSeen(sessionId: Int, lastSeen: Instant): Boolean
    fun deleteByUserId(userId: Int): Int
}