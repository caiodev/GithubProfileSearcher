package githubprofilesearcher.caiodev.com.br.githubprofilesearcher

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_show_repository_info.*

class ShowRepositoryInfoActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_repository_info)

        println("LINK: ${intent?.extras?.getString("repo_url")}")

        githubRepositoryWebView.loadUrl(intent?.extras?.getString("repo_url"))
        githubRepositoryWebView.settings.javaScriptEnabled = true
        githubRepositoryWebView.webViewClient = WebViewClient()
    }
}