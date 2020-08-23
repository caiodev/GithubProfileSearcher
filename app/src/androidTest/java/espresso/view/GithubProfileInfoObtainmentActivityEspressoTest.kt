package espresso.view

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.GithubProfileListingActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GithubProfileInfoObtainmentActivityEspressoTest {

    @get:Rule
    internal val rule = activityScenarioRule<GithubProfileListingActivity>()

    private val countingIdlingResource = CountingIdlingResource("ProfileSearch")

    @Before
    fun setup() {
        IdlingRegistry.getInstance().register(countingIdlingResource)
        rule.scenario.onActivity {
            it.bindIdlingResource(countingIdlingResource)
        }
    }

    @Test
    fun searchProfile() {

        onView(withId(R.id.searchProfileTextInputEditText))
            .perform(typeText("torvalds"))

        onView(withId(R.id.actionIconImageView))
            .perform(click())
    }

    @After
    fun cleanUp() {
        IdlingRegistry.getInstance().unregister(countingIdlingResource)
    }
}
