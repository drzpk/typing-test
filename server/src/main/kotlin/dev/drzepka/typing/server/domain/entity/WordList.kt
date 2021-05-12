package dev.drzepka.typing.server.domain.entity

import dev.drzepka.typing.server.domain.value.Language

class WordList : AbstractEntity<Int>() {
    var name = ""
    var language = Language.ENGLISH
}