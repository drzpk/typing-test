package dev.drzepka.typing.server.infrastructure.service

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.drzepka.typing.server.domain.entity.table.UsersTable
import dev.drzepka.typing.server.domain.service.DatabaseProviderService
import dev.drzepka.typing.server.domain.util.Logger
import io.ktor.config.*
import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import javax.sql.DataSource

class DatabaseProviderServiceImpl(config: ApplicationConfig) : DatabaseProviderService {

    override val db: Database

    private val log by Logger()

    init {
        val dataSource = getDataSource(config)

        log.info("Updating the database")
        val liquibaseDatabase = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(JdbcConnection(dataSource.connection))
        val liquibase = Liquibase("classpath:/liquibase-changelog.xml", ClassLoaderResourceAccessor(), liquibaseDatabase)
        liquibase.update(Contexts(), LabelExpression())

        log.info("Creating the database connection")
        db = Database.connect(dataSource)
    }

    private fun getDataSource(config: ApplicationConfig): DataSource {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = config.property(JDBC_URL).getString()
            driverClassName = config.property(DRIVER_CLASS_NAME).getString()
            username = config.property(USERNAME).getString()
            password = config.property(PASSWORD).getString()
            maximumPoolSize = config.property(MAXIMUM_POOL_SIZE).getString().toInt()
        }

        log.info("Creating datasource to database {} with user {}", hikariConfig.jdbcUrl, hikariConfig.username)
        return HikariDataSource(hikariConfig)
    }

    companion object {
        private const val JDBC_URL = "database.jdbcUrl"
        private const val DRIVER_CLASS_NAME = "database.driverClassName"
        private const val USERNAME = "database.username"
        private const val PASSWORD = "database.password"
        private const val MAXIMUM_POOL_SIZE = "database.maximumPoolSize"
    }
}