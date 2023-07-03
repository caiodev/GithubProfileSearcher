package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun ViewModel.runTaskOnBackground(task: suspend () -> Unit) {
    viewModelScope.launch {
        task()
    }
}

fun ViewModel.runTaskOnForeground(task: suspend () -> Unit) {
    runBlocking {
        task()
    }
}
