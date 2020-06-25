package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.showUserRepositoryInformation.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.githubProfileUrl

class RepositoryInfoActivity : AppCompatActivity() {

    private lateinit var browserIntent: Intent

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

        var isChromeInstalled = false

        val activityIntent =
            Intent().setAction(Intent.ACTION_VIEW).addCategory(Intent.CATEGORY_BROWSABLE).setData(
                Uri.fromParts("http", "", null)
            )

        applicationContext.packageManager.queryIntentActivities(
            activityIntent,
            0
        ).forEach {
            if (it.activityInfo.packageName.contains("com.android.chrome") ||
                it.activityInfo.packageName.contains("com.chrome.beta") ||
                it.activityInfo.packageName.contains("com.chrome.dev") ||
                it.activityInfo.packageName.contains("com.google.android.apps.chrome")
            ) {
                isChromeInstalled = true
            }
        }

        return isChromeInstalled
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