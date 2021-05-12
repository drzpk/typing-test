package dev.drzepka.typing.server.application.configuration

import dev.drzepka.typing.server.application.service.*
import dev.drzepka.typing.server.domain.repository.UserRepository
import dev.drzepka.typing.server.domain.repository.WordListRepository
import dev.drzepka.typing.server.domain.repository.WordRepository
import dev.drzepka.typing.server.infrastructure.PBKDF2HashService
import dev.drzepka.typing.server.infrastructure.repository.ExposedUserRepository
import dev.drzepka.typing.server.infrastructure.repository.ExposedWordListRepository
import dev.drzepka.typing.server.infrastructure.repository.ExposedWordRepository
import dev.drzepka.typing.server.infrastructure.service.DatabaseProviderServiceImpl
import io.ktor.application.*
import org.koin.core.module.Module
import org.koin.dsl.module

fun Application.typingServerKoinModule(): Module = module {

    // Application
    single { WordBulkManagementService(get(), get()) }

    // Domain
    single { UserService(get(), get()) }
    single { WordListService(get()) }
    single { WordService(get(), get()) }

    // Infrastructure
    single<HashService> { PBKDF2HashService() }
    single<UserRepository> { ExposedUserRepository() }
    single<WordListRepository> { ExposedWordListRepository() }
    single<WordRepository> { ExposedWordRepository() }

    val databaseProviderServiceImpl = DatabaseProviderServiceImpl(environment.config)
    single<DatabaseProviderService> { databaseProviderServiceImpl }
}
