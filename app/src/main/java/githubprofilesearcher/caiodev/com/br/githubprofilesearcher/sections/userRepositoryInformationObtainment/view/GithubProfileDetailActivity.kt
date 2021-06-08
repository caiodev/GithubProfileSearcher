package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.userRepositoryInformationObtainment.view

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R

class GithubProfileDetailActivity : AppCompatActivity(R.layout.activity_profile_detail) {

    private val emptyString = ""
    private val https = "https"
    private val stable = "com.android.chrome"
    private val beta = "com.chrome.beta"
    private val dev = "com.chrome.dev"
    private val local = "com.google.android.apps.chrome"
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
        val activityIntent =
            Intent().setAction(Intent.ACTION_VIEW).addCategory(Intent.CATEGORY_BROWSABLE).setData(
                Uri.fromParts(https, emptyString, null)
            )
        return isChromeInstalled(
            packageManager.queryIntentActivities(
                activityIntent,
                0
            )
        )
    }

    private fun isChromeInstalled(resolveInfoList: List<ResolveInfo>): Boolean {

        var isChromeInstalled = false

        val packageCount = resolveInfoList.count {
            it.activityInfo.packageName.contains(stable) ||
                it.activityInfo.packageName.contains(beta) ||
                it.activityInfo.packageName.contains(dev) ||
                it.activityInfo.packageName.contains(local)
        }

        if (packageCount >= 1) {
            isChromeInstalled = true
        }

        return isChromeInstalled
    }

    private fun launchChromeCustomTab() {
        CustomTabsIntent.Builder()
            .setDefaultColorSchemeParams(
                CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(ContextCompat.getColor(applicationContext, R.color.black))
                    .build()
            )
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

    companion object {
        private const val githubProfileUrl = "githubProfileUrl"

        fun newIntent(applicationContext: Context, profileUrl: String) =
            Intent(
                applicationContext,
                GithubProfileDetailActivity::class.java
            ).putExtra(githubProfileUrl, profileUrl)
    }
}
