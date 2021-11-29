package dev.drzepka.typing.server.application

data class TypingTestSession(val sessionId: Int, val userId: Int, val isAdmin: Boolean)