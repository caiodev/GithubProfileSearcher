package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions

import androidx.lifecycle.LiveData
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.LiveEvent

fun <T> LiveData<T>.toImmutableSingleEvent(): LiveData<T> {
    val result = LiveEvent<T>()
    result.addSource(this) {
        result.value = it
    }
    return result
}