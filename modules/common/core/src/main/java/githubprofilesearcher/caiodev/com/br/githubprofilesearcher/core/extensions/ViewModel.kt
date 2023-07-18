package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

inline fun ViewModel.runTaskOnBackground(crossinline task: suspend () -> Unit) {
    viewModelScope.launch { task() }
}

inline fun ViewModel.runTaskOnForeground(crossinline task: suspend () -> Unit) {
    runBlocking { task() }
}