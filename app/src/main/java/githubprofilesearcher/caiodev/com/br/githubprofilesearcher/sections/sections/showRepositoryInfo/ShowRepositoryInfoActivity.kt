package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.showRepositoryInfo

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.InflateException
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import kotlinx.android.synthetic.main.activity_show_repository_info.*

class ShowRepositoryInfoActivity : AppCompatActivity() {

    private var webViewClient: WebViewClient? = null
    private var browserIntent: Intent? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            setContentView(githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R.layout.activity_show_repository_info)
            githubRepositoryWebView.loadUrl(
                intent?.extras?.getString(
                    getString(
                        githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R.string.repository_opening_intent
                    )
                )
            )
            githubRepositoryWebView.settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
            githubRepositoryWebView.webViewClient = webViewClient
        } catch (exception: Exception) {
            when (exception) {
                is InflateException -> {
                    browserIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(intent?.extras?.getString(getString(R.string.repository_opening_intent)))
                    )
                    startActivity(browserIntent)
                    finish()
                }
            }
        }
    }
}