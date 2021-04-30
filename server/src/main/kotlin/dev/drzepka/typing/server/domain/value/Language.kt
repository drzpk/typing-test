package dev.drzepka.typing.server.domain.value

/**
 * Language codes are encoded in the ISO 639-1 standard:
 * https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
 */
enum class Language(val symbol: String) {

    ENGLISH("en"),
    POLISH("pl");

    companion object {
        fun fromSymbol(str: String): Language? = values().firstOrNull { it.symbol == str }
    }
}