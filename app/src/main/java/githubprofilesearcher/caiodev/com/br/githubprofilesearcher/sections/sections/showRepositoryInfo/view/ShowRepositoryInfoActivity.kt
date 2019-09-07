package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.showRepositoryInfo.view

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.InflateException
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.githubProfileUrl
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.setViewVisibility
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.showSnackBar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.network.NetworkChecking
import kotlinx.android.synthetic.main.activity_show_repository_info.*
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

class ShowRepositoryInfoActivity : AppCompatActivity() {

    private var browserIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkInternetConnection { openWebview() }
    }

    private fun checkInternetConnection(genericFunction: () -> Unit) {
        if (NetworkChecking.isInternetConnectionAvailable(applicationContext))
            genericFunction.invoke()
        else {
            if (noInternetConnectionWebViewLayout.visibility != VISIBLE)
                setViewVisibility(noInternetConnectionWebViewLayout, VISIBLE)
            showSnackBar(this, getString(R.string.no_connection_error))
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun openWebview() {
        try {
            setContentView(R.layout.activity_show_repository_info)
            githubRepositoryWebView.loadUrl(intent?.extras?.getString(githubProfileUrl))
            githubRepositoryWebView.settings.javaScriptEnabled = true
            githubRepositoryWebView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    setViewVisibility(webViewProgressBar, INVISIBLE)
                }
            }
        } catch (exception: Exception) {
            when (exception) {
                is InflateException -> {
                    browserIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(intent?.extras?.getString(githubProfileUrl))
                    )
                    startActivity(browserIntent)
                    finish()
                }

                is SocketTimeoutException -> showSnackBar(this, getString(R.string.no_connection_error))
                is UnknownHostException -> showSnackBar(this, getString(R.string.no_connection_error))
                is SSLHandshakeException -> showSnackBar(this, getString(R.string.no_connection_error))
            }
        }
    }
}