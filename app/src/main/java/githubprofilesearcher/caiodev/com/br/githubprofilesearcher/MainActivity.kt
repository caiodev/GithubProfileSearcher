package githubprofilesearcher.caiodev.com.br.githubprofilesearcher

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    //Attributes
    private val baseUrlAddress = "https://api.github.com/users/"
    private val githubUserPage = "https://github.com/"
    private var isUserInfoLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchProfile?.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                run()
                hideKeyboard()
                return@OnEditorActionListener true
            }
            false
        })

        cardUserInfo.setOnClickListener {
            if (isUserInfoLoaded) {
                openRepoPage(githubUserPage + searchProfile.text.toString())

            } else {
                toastMaker(getString(R.string.empty_card_view_error))
            }
        }
    }

    //This method gets the text inserted into the mSearchProfile EditText, besides,
    //it checks first, if there is internet connection and second, if the EditText is empty, if these two
    //conditions are satisfied, the EditText's content will be obtained
    private fun run() {
        if (checkInternetConnection()) {
            if (!isFieldEmpty(searchProfile)) {
                fillProfileInfo(searchProfile?.text.toString())
            }
        } else {
            toastMaker(getString(R.string.no_connection_error))
        }
    }

    //This method fills the requested github profile info
    private fun fillProfileInfo(profile: String) {
        val requestUrl = baseUrlAddress + profile

        val client = OkHttpClient()
        val request = Request.Builder()
            .get()
            .url(requestUrl)
            .build()

        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.i("getProfileInfo", "FAIL")
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {

                val jsonData = response.body().string()
                Log.i("getProfileInfo", jsonData)

                if (response.isSuccessful) {

                    runOnUiThread {
                        try {

                            val rootObj = JSONObject(jsonData)

                            userName?.text = rootObj.getString(getString(R.string.login_alias))

                            if (rootObj.getString(getString(R.string.bio_alias)) == getString(R.string.null_string)) {
                                userBio?.text = getString(R.string.null_bio_message)

                            } else {
                                userBio?.text = rootObj.getString(getString(R.string.bio_alias))
                            }

                            if (rootObj.getInt(getString(R.string.followers_alias)) <= 1) {
                                userFollowers?.text = getString(
                                    R.string.number_of_followers_passing_attributes,
                                    rootObj.getInt(getString(R.string.followers_alias)),
                                    getString(R.string.number_of_followers_singular)
                                )

                            } else {
                                userFollowers?.text = getString(
                                    R.string.number_of_followers_passing_attributes,
                                    rootObj.getInt(getString(R.string.followers_alias)),
                                    getString(R.string.number_of_followers_plural)
                                )
                            }

                            if (rootObj.getInt(getString(R.string.public_repos_alias)) <= 1) {
                                userRepos?.text = getString(
                                    R.string.number_of_repos_passing_attributes,
                                    rootObj.getInt(getString(R.string.public_repos_alias)),
                                    getString(R.string.number_of_repos_singular)
                                )

                            } else {
                                userRepos?.text = getString(
                                    R.string.number_of_repos_passing_attributes,
                                    rootObj.getInt(getString(R.string.public_repos_alias)),
                                    getString(R.string.number_of_repos_plural)
                                )
                            }

                            try {
                                Glide.with(this@MainActivity)
                                    .load(rootObj.getString(getString(R.string.avatar_image_url)))
                                    .asBitmap()
                                    .fitCenter()
                                    .into(userAvatar)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            cardUserInfo.visibility = View.VISIBLE
                            fillRepoInfo(rootObj.getString(getString(R.string.login_alias)))

                            isUserInfoLoaded = true

                        } catch (e: JSONException) {
                            cardUserInfo.visibility = View.GONE
                        }
                    }
                }
            }
        })
    }

    //This method fills the requested github Repo information as Repo name, repo URL and so on
    private fun fillRepoInfo(userName: String) {

        val requestUrl = "$baseUrlAddress$userName/repos"

        val client = OkHttpClient()
        val request = Request.Builder()
            .get()
            .url(requestUrl)
            .build()

        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.i("getProfileInfo", "FAIL")
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {

            }
        })
    }

    private fun openRepoPage(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)))
    }

    //This method checks if there is an empty field based on the EditText object received
    private fun isFieldEmpty(textField: EditText?) =
        if (textField?.text.toString() == "") {
            textField?.error = getString(R.string.empty_field_error)
            true
        } else {
            false
        }

    private fun hideKeyboard() {
        with(getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager) {
            hideSoftInputFromWindow(searchProfile.applicationWindowToken, 0)
        }
    }

    //It checks whether internet connection is available or not
    private fun checkInternetConnection() =
        with(getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager) {
            activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

    //It pops up a message received as parameter
    private fun toastMaker(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}