package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@Suppress("UNUSED")
inline fun <reified T> ViewModel.castAttributeThroughViewModel(attribute: Any?) =
    attribute as T

fun ViewModel.runTaskOnBackground(task: suspend () -> Unit) {
    viewModelScope.launch {
        task.invoke()
    }
}
