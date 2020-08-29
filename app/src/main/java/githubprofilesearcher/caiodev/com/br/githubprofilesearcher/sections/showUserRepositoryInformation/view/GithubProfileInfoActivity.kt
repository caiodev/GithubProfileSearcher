package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.showUserRepositoryInformation.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.showUserRepositoryInformation.viewModel.UserRepositoryInformationViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.emptyString
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.githubProfileUrl
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.https
import org.koin.androidx.viewmodel.ext.android.viewModel

class GithubProfileInfoActivity : AppCompatActivity() {

    private lateinit var browserIntent: Intent

    private val viewModel by viewModel<UserRepositoryInformationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openWebView()
    }

    private fun openWebView() {
        if (isChromeInstalled()) {
            launchChromeCustomTab()
        } else {
            launchBrowser()
        }
        finish()
    }

    private fun isChromeInstalled(): Boolean {
        val activityIntent =
            Intent().setAction(Intent.ACTION_VIEW).addCategory(Intent.CATEGORY_BROWSABLE).setData(
                Uri.fromParts(https, emptyString, null)
            )

        return viewModel.isChromeInstalled(
            applicationContext.packageManager.queryIntentActivities(
                activityIntent,
                0
            )
        )
    }

    private fun launchChromeCustomTab() {
        CustomTabsIntent.Builder()
            .setToolbarColor(ContextCompat.getColor(applicationContext, R.color.black))
            .setShowTitle(true)
            .setStartAnimations(
                applicationContext,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
            .setExitAnimations(
                applicationContext,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
            .build()
            .launchUrl(this, Uri.parse(intent?.extras?.getString(githubProfileUrl)))
    }

    private fun launchBrowser() {
        browserIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(intent?.extras?.getString(githubProfileUrl))
        )
        startActivity(browserIntent)
    }
}
