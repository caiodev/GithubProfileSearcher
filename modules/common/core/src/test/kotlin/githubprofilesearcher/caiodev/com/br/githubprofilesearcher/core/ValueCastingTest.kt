package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.cast.ValueCasting
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.types.number.defaultInteger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ValueCastingTest {
    @Test
    fun `castToNullable should return a valid value`() {
        ValueCasting.castTo<List<Int>>(arrayListOf(defaultInteger()))
            ?.first()
            ?.apply { assertEquals(defaultInteger(), this) }
    }

    @Test
    fun `castToNonNullable should return a valid value`() {
        ValueCasting.castToNonNullable<List<Int>>(arrayListOf(defaultInteger()))
            .first()
            .apply { assertEquals(defaultInteger(), this) }
    }
}
