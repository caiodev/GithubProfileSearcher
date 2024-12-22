package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.extensions.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.utils.types.number.one
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
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

inline fun <reified T> ViewModel.mutableSharedFlowOf() = MutableSharedFlow<T>(replay = one())
