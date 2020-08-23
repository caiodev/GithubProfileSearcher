package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.liveEvent

import androidx.lifecycle.Observer

class ObserverWrapper<T>(val observer: Observer<T>) : Observer<T> {

    private var pending = false

    override fun onChanged(t: T?) {
        if (pending) {
            pending = false
            observer.onChanged(t)
        }
    }

    fun newValue() {
        pending = true
    }
}
