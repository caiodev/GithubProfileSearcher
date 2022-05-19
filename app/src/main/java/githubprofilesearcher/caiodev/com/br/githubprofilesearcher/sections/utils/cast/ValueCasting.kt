package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.cast

object ValueCasting {
    @Suppress("UNCHECKED_CAST")
    fun <T> castToNonNullable(value: Any?) = value as T

    inline fun <reified T> castTo(value: Any?) = value as? T
}
