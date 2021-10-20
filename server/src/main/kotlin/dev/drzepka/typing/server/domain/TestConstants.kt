package dev.drzepka.typing.server.domain

object TestConstants {
    /**
     * "Weight" of backspace key in terms of incorrect keystrokes.
     *
     * Example: if backspace weight is 0.5 and backspace count is 10, these backspace hits are treated as if they
     * were 5 incorrect keystrokes.
     */
    const val BACKSPACE_INCORRECT_KEYSTROKE_WEIGHT = 0.5f

    /**
     * How many characters (on average) are there in a single word. Used to calculate the words-per-minute speed.
     * Value of this variable was set based on other online typing test available on the Internet
     * to ensure consistency.
     */
    const val CHARACTERS_PER_WORD = 5
}