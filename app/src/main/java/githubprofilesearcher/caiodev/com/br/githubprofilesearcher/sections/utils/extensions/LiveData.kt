package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions

import androidx.lifecycle.LiveData
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.liveEvent.SingleLiveEvent

fun <T> LiveData<T>.toImmutableSingleLiveEvent(): LiveData<T> {
    val result =
        SingleLiveEvent<T>()
    result.addSource(this) {
        result.value = it
    }
    return result
}
