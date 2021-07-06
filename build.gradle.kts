allprojects {
    group = "dev.drzepka.typing"
    version = "1.1.0-SNAPSHOT"
}

subprojects {
    repositories {
        mavenLocal()
        jcenter()
        maven { url = uri("https://kotlin.bintray.com/ktor") }
    }
}

buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        val koinVersion: String by project
        classpath("org.koin:koin-gradle-plugin:$koinVersion")
    }
}