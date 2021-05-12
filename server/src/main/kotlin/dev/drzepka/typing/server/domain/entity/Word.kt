package dev.drzepka.typing.server.domain.entity

class Word : AbstractEntity<Int>() {
    var word = ""
    var popularity = 0
    var wordListId = 0
}