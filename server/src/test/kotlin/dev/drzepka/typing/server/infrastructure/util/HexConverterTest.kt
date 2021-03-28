package dev.drzepka.typing.server.infrastructure.util

import org.assertj.core.api.BDDAssertions.assertThatIllegalArgumentException
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class HexConverterTest {

    @Test
    fun `should convert to hex string`() {
        val result = HexConverter.toHexString(byteArrayOf(-103, -98, 79))
        then(result).isEqualTo("999e4f")
    }

    @Test
    fun `should convert from hex string`() {
        val result = HexConverter.fromHexString("f90185ac")
        then(result).isEqualTo(byteArrayOf(-7, 1, -123, -84))
    }

    @Test
    fun `should throw exception on malformed input`() {
        assertThatIllegalArgumentException().isThrownBy {
            HexConverter.fromHexString("123")
        }
        assertThatIllegalArgumentException().isThrownBy {
            HexConverter.fromHexString("fagg")
        }
    }
}