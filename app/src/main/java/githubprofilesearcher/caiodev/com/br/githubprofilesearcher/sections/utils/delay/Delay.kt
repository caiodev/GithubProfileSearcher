package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.delay

import java.util.*
import kotlin.concurrent.schedule

object Delay {

    fun delay(timer: Timer, milliseconds: Long, action: () -> Unit) {
        timer.schedule(milliseconds) { action.invoke() }
    }
}