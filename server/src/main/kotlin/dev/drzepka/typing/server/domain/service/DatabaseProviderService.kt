package dev.drzepka.typing.server.domain.service

import org.jetbrains.exposed.sql.Database

interface DatabaseProviderService {
    val db: Database
}