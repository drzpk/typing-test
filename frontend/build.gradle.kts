tasks.register<Exec>("npmBuild") {
    workingDir("${project.projectDir}/web")
    commandLine("cmd", "/c", "npm run build")
}