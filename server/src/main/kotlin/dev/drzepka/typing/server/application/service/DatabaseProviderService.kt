package dev.drzepka.typing.server.application.service

import org.jetbrains.exposed.sql.Database

interface DatabaseProviderService {
    val db: Database
}