package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.utils.cast

inline fun <reified T> Any?.castTo() = this as? T

@Suppress("UNCHECKED_CAST")
fun <T> Any.castToNonNullable() = this as T
