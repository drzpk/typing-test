package dev.drzepka.typing.server.domain.entity

import java.time.Instant

class Session(var userId: Int) : AbstractEntity<Int>() {
    var createdAt: Instant = Instant.now()
    var lastSeen: Instant = Instant.now()

    var ipAddress = ""
    var userAgent = ""
}