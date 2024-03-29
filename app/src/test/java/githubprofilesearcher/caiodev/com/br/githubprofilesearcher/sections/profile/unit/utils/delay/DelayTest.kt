package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.unit.utils.delay

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.delay.Delay.delayTaskBy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import utils.base.TestSteps
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit.MILLISECONDS

class DelayTest : TestSteps {

    override fun setupDependencies() {
        //
    }

    @Test
    fun delay_timeAndInstructionToExecute_executeAGenericOperation() {
        var countDownLatch: CountDownLatch? = null

        given {
            countDownLatch = CountDownLatch(1)
        }

        doWhen {
            delayTaskBy(100) { countDownLatch?.countDown() }
        }

        then {
            countDownLatch?.await(200, MILLISECONDS)
            assertEquals(0, countDownLatch?.count)
        }
    }
}
