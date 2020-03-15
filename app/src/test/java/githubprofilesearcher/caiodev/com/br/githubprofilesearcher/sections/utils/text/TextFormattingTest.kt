package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.text

import utils.base.TestSteps
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TextFormattingTest : TestSteps {

    private lateinit var textFormatting: TextFormatting

    @BeforeEach
    override fun setupDependencies() {
        textFormatting = TextFormatting
    }

    @Test
    fun concatenateStrings_templateAndTextToFormat_returnsFormattedText() {

        var finalString = ""

        doWhen {
            finalString = textFormatting.concatenateStrings("ConcatTest: %s", "TestYourMight")
        }

        then {
            assertEquals("ConcatTest: TestYourMight", finalString)
        }
    }

    @Test
    fun formatNumber_numberToFormat_returnsFormattedNumberWithTwoDecimalPlacesAsString() {

        var finalFormattedNumber = ""

        doWhen {
            finalFormattedNumber = textFormatting.formatNumber(125.611)
        }

        then {
            assertEquals("125.61", finalFormattedNumber)
        }
    }
}