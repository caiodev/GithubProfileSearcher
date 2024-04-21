package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.cast.ValueCasting
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.types.number.defaultInteger
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

internal class ValueCastingTest : FunSpec({
    test("castTo should return a valid value") {
        ValueCasting.castTo<List<Int>>(arrayListOf(defaultInteger()))
            ?.first()
            .shouldBe(defaultInteger())
    }

    test("castToNonNullable should return a valid value") {
        ValueCasting.castToNonNullable<List<Int>>(arrayListOf(defaultInteger()))
            .first()
            .shouldBe(defaultInteger())
    }
})
