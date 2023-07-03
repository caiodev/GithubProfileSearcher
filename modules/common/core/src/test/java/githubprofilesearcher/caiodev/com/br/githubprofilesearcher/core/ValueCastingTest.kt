package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.cast.ValueCasting
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

internal class ValueCastingTest : FunSpec({
    test("castToNonNullable returns valid value") {
        ValueCasting.castToNonNullable<List<Int>>(arrayListOf(0)).first().shouldBe(0)
    }
})
