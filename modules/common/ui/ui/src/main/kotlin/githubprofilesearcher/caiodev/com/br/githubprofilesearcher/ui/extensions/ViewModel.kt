package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.types.number.one
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

inline fun ViewModel.runTaskOnBackground(
    dispatcher: CoroutineContext,
    crossinline task: suspend () -> Unit,
) {
    viewModelScope.launch(dispatcher) {
        task()
    }
}

fun ViewModel.onCoroutineStatusRequested(): Boolean = viewModelScope.apply { ensureActive() }.isActive

inline fun <reified T> ViewModel.mutableSharedFlowOf() = MutableSharedFlow<T>(replay = one())
