package dev.drzepka.typing.server.application.configuration

import dev.drzepka.typing.server.application.service.WordBulkManagementService
import dev.drzepka.typing.server.domain.service.*
import dev.drzepka.typing.server.infrastructure.PBKDF2HashService
import dev.drzepka.typing.server.infrastructure.service.DatabaseProviderServiceImpl
import io.ktor.application.*
import org.koin.core.module.Module
import org.koin.dsl.module

fun Application.typingServerKoinModule(): Module = module {

    // Application
    single { WordBulkManagementService(get(), get()) }

    // Domain
    single { UserService(get()) }
    single { WordListService() }
    single { WordService() }

    // Infrastructure
    single<HashService> { PBKDF2HashService() }

    val databaseProviderServiceImpl = DatabaseProviderServiceImpl(environment.config)
    single<DatabaseProviderService> { databaseProviderServiceImpl }
}
