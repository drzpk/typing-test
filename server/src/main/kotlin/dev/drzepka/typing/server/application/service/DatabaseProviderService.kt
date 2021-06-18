package dev.drzepka.typing.server.application.service

import org.jetbrains.exposed.sql.Database

/**
 * Provides database connection.
 */
interface DatabaseProviderService {
    val db: Database
}