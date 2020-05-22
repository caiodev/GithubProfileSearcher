package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.delay

import java.util.*
import kotlin.concurrent.schedule

object Delay {

    inline fun delay(milliseconds: Long, crossinline action: () -> Unit) {
        Timer().schedule(milliseconds) { action.invoke() }
    }
}