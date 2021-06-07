package dev.drzepka.typing.server.application.configuration

import dev.drzepka.typing.server.application.service.*
import dev.drzepka.typing.server.domain.repository.*
import dev.drzepka.typing.server.domain.service.TestService
import dev.drzepka.typing.server.infrastructure.PBKDF2HashService
import dev.drzepka.typing.server.infrastructure.repository.*
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
    single { TestManagerService(get(), get(), get(), get()) }
    single { TestResultService(get()) }
    single { TestStatsService(get(), get()) }

    // Domain
    single { TestService(get(), get()) }

    // Infrastructure
    single<HashService> { PBKDF2HashService() }
    single<ConfigurationRepository> { object : ConfigurationRepository {} }

    single<UserRepository> { ExposedUserRepository() }
    single<WordListRepository> { ExposedWordListRepository() }
    single<WordRepository> { ExposedWordRepository() }
    single<TestDefinitionRepository> { ExposedTestDefinitionRepository(get()) }
    single<TestRepository> { ExposedTestRepository(get(), get()) }
    single<TestResultRepository> { ExposedTestResultRepository(get()) }

    val databaseProviderServiceImpl = DatabaseProviderServiceImpl(environment.config)
    single<DatabaseProviderService> { databaseProviderServiceImpl }
}
