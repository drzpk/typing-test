package dev.drzepka.typing.server.application.configuration

import dev.drzepka.typing.server.application.service.*
import dev.drzepka.typing.server.domain.dao.TestResultDAO
import dev.drzepka.typing.server.domain.repository.*
import dev.drzepka.typing.server.domain.service.TestScoreCalculatorService
import dev.drzepka.typing.server.domain.service.TestService
import dev.drzepka.typing.server.infrastructure.PBKDF2HashService
import dev.drzepka.typing.server.infrastructure.dao.ExposedTestResultDAO
import dev.drzepka.typing.server.infrastructure.repository.*
import dev.drzepka.typing.server.infrastructure.service.DatabaseProviderServiceImpl
import dev.drzepka.typing.server.infrastructure.service.TransactionService
import io.ktor.application.*
import org.koin.core.module.Module
import org.koin.dsl.module

fun Application.typingServerKoinModule(): Module = module {

    // Application
    single { WordImportService(get(), get()) }
    single { WordExportService(get()) }
    single { UserService(get(), get(), get(), get(), get()) }
    single { WordListService(get(), get()) }
    single { WordService(get(), get()) }
    single { TestDefinitionService(get(), get(), get(), get()) }
    single { TestManagerService(get(), get(), get(), get()) }
    single { TestResultService(get(), get(), get(), get()) }
    single { TestStatsService(get(), get()) }
    single { UserSessionService(get(), get()) }
    single { ApplicationSessionService(get()) }

    // Domain
    single { dev.drzepka.typing.server.domain.service.TestDefinitionService(get()) }
    single { TestService(get(), get(), get()) }
    single { TestScoreCalculatorService() }

    // Infrastructure
    single<HashService> { PBKDF2HashService() }
    single<ConfigurationRepository> { object : ConfigurationRepository {} }
    single { TransactionService() }

    single<UserRepository> { ExposedUserRepository() }
    single<WordListRepository> { ExposedWordListRepository() }
    single<WordRepository> { ExposedWordRepository() }
    single<TestDefinitionRepository> { ExposedTestDefinitionRepository(get()) }
    single<TestRepository> { ExposedTestRepository(get(), get()) }
    single<TestResultRepository> { ExposedTestResultRepository(get()) }
    single<SessionRepository> { ExposedSessionRepository() }

    single<TestResultDAO> { ExposedTestResultDAO() }

    val databaseProviderServiceImpl = DatabaseProviderServiceImpl(environment.config)
    single<DatabaseProviderService> { databaseProviderServiceImpl }
}
