package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions

import androidx.lifecycle.ViewModel

@Suppress("UNUSED")
inline fun <reified T> ViewModel.castAttributeThroughViewModel(attribute: Any?) =
    attribute as T