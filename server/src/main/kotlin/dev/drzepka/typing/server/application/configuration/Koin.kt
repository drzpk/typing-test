package dev.drzepka.typing.server.application.configuration

import dev.drzepka.typing.server.domain.service.DatabaseProviderService
import dev.drzepka.typing.server.domain.service.HashService
import dev.drzepka.typing.server.domain.service.UserService
import dev.drzepka.typing.server.infrastructure.PBKDF2HashService
import dev.drzepka.typing.server.infrastructure.service.DatabaseProviderServiceImpl
import io.ktor.application.*
import org.koin.core.module.Module
import org.koin.dsl.module

fun Application.typingServerKoinModule(): Module = module {

    // Domain
    single { UserService(get()) }

    // Infrastructure
    single<HashService> { PBKDF2HashService() }

    val databaseProviderServiceImpl = DatabaseProviderServiceImpl(environment.config)
    single<DatabaseProviderService> { databaseProviderServiceImpl }
}
