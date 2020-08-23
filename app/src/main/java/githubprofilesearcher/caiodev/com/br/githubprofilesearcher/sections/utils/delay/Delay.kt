package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.delay

import java.util.Timer
import kotlin.concurrent.schedule

object Delay {

    inline fun delayTask(milliseconds: Long, crossinline action: () -> Unit) {
        Timer().schedule(milliseconds) { action.invoke() }
    }
}
