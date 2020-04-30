package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.delay

import java.util.*
import kotlin.concurrent.schedule

object Delay {

    inline fun delay(timer: Timer, milliseconds: Long, crossinline action: () -> Unit) {
        timer.schedule(milliseconds) { action.invoke() }
    }
}