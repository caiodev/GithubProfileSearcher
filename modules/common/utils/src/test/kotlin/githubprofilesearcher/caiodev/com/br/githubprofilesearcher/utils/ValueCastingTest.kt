package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.utils

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.utils.cast.castTo
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.utils.cast.castToNonNullable
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.utils.types.number.defaultInteger
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ValueCastingTest {
    @Test
    fun `castToNullable should return a valid value`() {
        arrayListOf(defaultInteger()).castTo<List<Int>>()
            ?.first()
            ?.apply { assertEquals(defaultInteger(), this) }
    }

    @Test
    fun `castToNonNullable should return a valid value`() {
        arrayListOf(defaultInteger()).castToNonNullable<List<Int>>()
            .first()
            .apply { assertEquals(defaultInteger(), this) }
    }
}
