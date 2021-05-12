package dev.drzepka.typing.server.application.configuration

import dev.drzepka.typing.server.application.service.*
import dev.drzepka.typing.server.domain.repository.TestDefinitionRepository
import dev.drzepka.typing.server.domain.repository.UserRepository
import dev.drzepka.typing.server.domain.repository.WordListRepository
import dev.drzepka.typing.server.domain.repository.WordRepository
import dev.drzepka.typing.server.infrastructure.PBKDF2HashService
import dev.drzepka.typing.server.infrastructure.repository.ExposedTestDefinitionRepository
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
    single { UserService(get(), get()) }
    single { WordListService(get()) }
    single { WordService(get(), get()) }
    single { TestDefinitionService(get(), get()) }

    // Infrastructure
    single<HashService> { PBKDF2HashService() }
    single<UserRepository> { ExposedUserRepository() }
    single<WordListRepository> { ExposedWordListRepository() }
    single<WordRepository> { ExposedWordRepository() }
    single<TestDefinitionRepository> { ExposedTestDefinitionRepository(get()) }

    val databaseProviderServiceImpl = DatabaseProviderServiceImpl(environment.config)
    single<DatabaseProviderService> { databaseProviderServiceImpl }
}
