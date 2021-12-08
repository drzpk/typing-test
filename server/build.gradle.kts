val logback_version: String by project
val ktor_version: String by project
val kotlin_version: String by project

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-allopen:1.4.30")
        classpath("com.github.jengelman.gradle.plugins:shadow:5.2.0")
    }
}

plugins {
    application
    java
    kotlin("jvm") version "1.4.30"
    kotlin("plugin.allopen") version "1.4.30"
    id("koin")
    id("org.liquibase.gradle") version "2.0.3"
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("org.jetbrains.dokka") version "1.4.32"
    id("com.google.cloud.tools.jib") version "3.1.4"
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
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.10.2")
    implementation("com.google.guava:guava:31.0.1-jre")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
    testRuntimeOnly("com.h2database:h2:1.3.176")
    testImplementation("org.assertj:assertj-core:3.19.0")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.koin:koin-test:$koinVersion")
    testImplementation("org.mockito:mockito-core:3.9.0")
    testImplementation("org.mockito:mockito-junit-jupiter:3.9.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:3.1.0")

    liquibaseRuntime("org.liquibase:liquibase-core:4.3.2")
    liquibaseRuntime("mysql:mysql-connector-java:8.0.19")

    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.4.32")
}

allOpen {
    annotation("dev.drzepka.typing.server.domain.util.Mockable")
}

liquibase {
    activities {
        create("main") {
            arguments = mapOf(
                "url" to "jdbc:mysql://localhost/typing_test",
                "username" to "typing_test",
                "password" to "typing_test",
                "changeLogFile" to "server/src/main/resources/liquibase-changelog.xml"
            )
        }
    }
}

jib {
    from {
        image = "adoptopenjdk/openjdk11:x86_64-ubuntu-jre-11.0.12_7"
    }
    to {
        image = "typingtest-server:${rootProject.version}"
    }
    container {
        mainClass = "io.ktor.server.tomcat.EngineMain"
    }
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

tasks.register<Copy>("embedFrontend") {
    dependsOn("deleteOldFrontendResources", ":frontend:npmBuild")
    from("${project.rootProject.projectDir}/frontend/web/dist")
    into("${project.buildDir}/resources/main/frontend-bundle")
}

tasks.register<Delete>("deleteOldFrontendResources") {
    delete("${project.buildDir}/resources/main/frontend-bundle")
}

tasks.findByName("shadowJar")?.dependsOn("embedFrontend")
listOf(tasks.findByName("jib"), tasks.findByName("jibDockerBuild")).forEach {
    it?.dependsOn("shadowJar")
}