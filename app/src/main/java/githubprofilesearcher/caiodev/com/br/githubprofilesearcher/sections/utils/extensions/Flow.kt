package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

fun <T> MutableStateFlow<T>.emitValue(value: T) {
    runBlocking {
        this@emitValue.emit(value)
    }
}
