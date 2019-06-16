package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.text

object TextFormatting {

    fun concatenateStrings(template: String, vararg text: String) = String.format(template, *text)

    fun formatNumber(number: Double) = String.format("%.2f", number)
}