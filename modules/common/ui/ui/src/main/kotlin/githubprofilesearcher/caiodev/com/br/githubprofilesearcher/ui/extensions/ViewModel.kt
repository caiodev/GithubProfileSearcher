package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private val coroutineStatusMutableStateFlow = MutableStateFlow(false)

fun ViewModel.runTaskOnBackground(
    dispatcher: CoroutineDispatcher,
    task: suspend () -> Unit,
) {
    viewModelScope.launch(dispatcher) {
        task()
        coroutineStatusMutableStateFlow.collect {
            if (it) {
                ensureActive()
                coroutineStatusMutableStateFlow.value = false
            }
        }
    }
}

fun ViewModel.onCoroutineStatusRequested(): Boolean {
    coroutineStatusMutableStateFlow.value = true
    return viewModelScope.isActive
}
