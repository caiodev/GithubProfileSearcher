package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.delay

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.delay.Delay.delay
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.base.TestSteps
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit.MILLISECONDS

class DelayTest : TestSteps {

    private lateinit var timer: Timer

    @BeforeEach
    override fun setupDependencies() {
        timer = Timer()
    }

    @Test
    fun delay_timeAndInstructionToExecute_executeTheGenericOperation() {

        var countDownLatch: CountDownLatch? = null

        given {
            countDownLatch = CountDownLatch(1)
        }

        doWhen {
            delay(timer, 100) { countDownLatch?.countDown() }
        }

        then {
            countDownLatch?.await(100, MILLISECONDS)
            assertEquals(0, countDownLatch?.count)
        }
    }
}