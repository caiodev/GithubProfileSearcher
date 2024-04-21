package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.cast

object ValueCasting {
    inline fun <reified T> castTo(value: Any?) = value as? T

    @Suppress("UNCHECKED_CAST")
    fun <T> castToNonNullable(value: Any) = value as T
}
