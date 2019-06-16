package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.showRepositoryInfo.view

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.InflateException
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.setViewVisibility
import kotlinx.android.synthetic.main.activity_show_repository_info.*

class ShowRepositoryInfoActivity : AppCompatActivity() {

    private var browserIntent: Intent? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            setContentView(R.layout.activity_show_repository_info)
            githubRepositoryWebView.loadUrl(intent?.extras?.getString(Constants.githubProfileUrl))
            githubRepositoryWebView.settings.javaScriptEnabled = true
            githubRepositoryWebView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    setViewVisibility(webViewProgressBar, View.INVISIBLE)
                }
            }
        } catch (exception: Exception) {
            when (exception) {
                is InflateException -> {
                    browserIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(intent?.extras?.getString(Constants.githubProfileUrl))
                    )
                    startActivity(browserIntent)
                    finish()
                }
            }
        }
    }
}