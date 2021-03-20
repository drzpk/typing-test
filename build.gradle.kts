allprojects {
    group = "dev.drzepka.typing"
    version = "1.0.0-SNAPSHOT"
}

subprojects {
    repositories {
        mavenLocal()
        jcenter()
        maven { url = uri("https://kotlin.bintray.com/ktor") }
    }
}