package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun ViewModel.runTaskOnBackground(task: suspend () -> Unit) {
    viewModelScope.launch {
        task()
    }
}

@Suppress("UNUSED")
fun ViewModel.runTaskOnForeground(task: suspend () -> Unit) {
    runBlocking {
        task()
    }
}
