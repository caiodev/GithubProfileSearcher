package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.text

import java.lang.String.format
import java.util.Locale.getDefault

object TextFormatting {

    fun concatenateStrings(template: String, vararg text: String) = format(template, *text)

    fun formatNumber(number: Double) = format(getDefault(), "%.2f", number)
}