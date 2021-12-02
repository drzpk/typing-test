package dev.drzepka.typing.server.infrastructure.repository

import dev.drzepka.typing.server.domain.entity.Session
import dev.drzepka.typing.server.domain.repository.SessionRepository
import dev.drzepka.typing.server.infrastructure.repository.table.Sessions
import dev.drzepka.typing.server.infrastructure.util.NullableForeignKeyWrapper
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.update
import java.time.Instant

class ExposedSessionRepository : SessionRepository {
    override fun save(session: Session) {
        if (session.isStored())
            throw IllegalArgumentException("Sessions can't be updated directly")

        val id = Sessions.insertAndGetId {
            sessionToRow(session, it)
        }

        session.id = id.value
    }

    override fun updateLastSeen(sessionId: Int, lastSeen: Instant): Boolean {
        val count = Sessions.update({ Sessions.id eq sessionId }) {
            it[Sessions.lastSeen] = lastSeen
        }

        return count > 0
    }

    private fun sessionToRow(session: Session, stmt: UpdateBuilder<Int>) {
        stmt[Sessions.userId] = NullableForeignKeyWrapper(session.userId)
        stmt[Sessions.createdAt] = session.createdAt
        stmt[Sessions.lastSeen] = session.lastSeen
        stmt[Sessions.ipAddress] = session.ipAddress
        stmt[Sessions.userAgent] = session.userAgent
    }
}