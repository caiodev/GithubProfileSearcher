package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.delay

import java.util.*
import kotlin.concurrent.schedule

object Delay {

    private val timer = Timer()

    fun delay(milliseconds: Long, action: () -> Unit) {
        timer.schedule(milliseconds) {
            action.invoke()
        }
    }
}