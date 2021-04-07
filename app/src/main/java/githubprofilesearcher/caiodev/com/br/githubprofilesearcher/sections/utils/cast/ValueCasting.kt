package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.cast

object ValueCasting {
    @Suppress("UNCHECKED_CAST")
    fun <T> castValue(value: Any?) = value as T
}
