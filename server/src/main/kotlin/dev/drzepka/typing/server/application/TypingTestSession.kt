package dev.drzepka.typing.server.application

data class TypingTestSession(val userSessionId: Int, val userId: Int?, val isAdmin: Boolean)