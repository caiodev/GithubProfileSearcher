package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.extensions.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

inline fun ViewModel.runTaskOnBackground(
    dispatcher: CoroutineContext,
    crossinline task: suspend () -> Unit,
) {
    viewModelScope.launch(dispatcher) {
        task()
        ensureActive()
    }
}
