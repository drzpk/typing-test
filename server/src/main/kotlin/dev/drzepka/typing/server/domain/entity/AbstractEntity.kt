package dev.drzepka.typing.server.domain.entity

abstract class AbstractEntity<T> {
    var id: T? = null

    fun isStored(): Boolean = id != null
}