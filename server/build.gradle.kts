val logback_version: String by project
val ktor_version: String by project
val kotlin_version: String by project

plugins {
    application
    kotlin("jvm") version "1.4.30"
    id("koin")
}

application {
    mainClassName = "io.ktor.server.tomcat.EngineMain"
}

dependencies {
    val koinVersion: String by project
    val exposedVersion: String by project

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("io.ktor:ktor-server-tomcat:$ktor_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-sessions:$ktor_version")
    implementation("io.ktor:ktor-jackson:$ktor_version")
    implementation("org.koin:koin-core:$koinVersion")
    implementation("org.koin:koin-ktor:$koinVersion")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("mysql:mysql-connector-java:8.0.19")
    implementation("com.zaxxer:HikariCP:3.4.2")
    implementation("org.liquibase:liquibase-core:4.3.2")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
    testRuntimeOnly("com.h2database:h2:1.3.176")
    testImplementation("org.assertj:assertj-core:3.19.0")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.koin:koin-test:$koinVersion")
}

configurations {
    all {
        exclude(group = "junit")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}