package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.delay

import java.util.Timer
import kotlin.concurrent.schedule

object Delay {
    inline fun delayTaskBy(
        milliseconds: Long,
        crossinline action: () -> Unit,
    ) {
        Timer().schedule(milliseconds) { action() }
    }
}
