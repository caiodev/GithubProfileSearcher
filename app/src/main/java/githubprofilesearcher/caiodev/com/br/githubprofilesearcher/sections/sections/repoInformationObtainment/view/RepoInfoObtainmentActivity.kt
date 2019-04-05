package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.repoInformationObtainment.view

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.repoInformationObtainment.model.UserRepositoryInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.repoInformationObtainment.viewModel.RepoInfoObtainmentViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.showRepositoryInfo.ShowRepositoryInfoActivity
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.ActivityFlow
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.*
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.imageLoading.LoadImage
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.network.NetworkChecking
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.projectKeys.SharedPreferencesReference
import kotlinx.android.synthetic.main.activity_main.*

class RepoInfoObtainmentActivity : AppCompatActivity(), ActivityFlow {

    private val viewModel: RepoInfoObtainmentViewModel by lazy {
        ViewModelProviders.of(this).get(RepoInfoObtainmentViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        handleViewModel()
    }

    override fun setupView() {

        searchIconImageView.setOnClickListener {
            searchProfile()
        }

        cardUserInfo.setOnClickListener {
            if (viewModel.isUserInfoLoaded()) {
                if (NetworkChecking.isInternetConnectionAvailable(applicationContext))
                    openRepoPage(
                        getValueFromSharedPreferencesThroughViewModel(
                            githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.projectKeys.SharedPreferences.githubProfileUrl, null
                        )
                    )
                else toastMaker(getString(R.string.no_connection_error))
            } else {
                toastMaker(getString(R.string.empty_card_view_error))
            }
        }

        searchProfileTextInputEditText?.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchProfile()
                return@OnEditorActionListener true
            }
            false
        })
    }

    override fun handleViewModel() {

        viewModel.getLiveData().observe(this, Observer { state ->

            when (state) {

                is UserRepositoryInformation -> {

                    viewModel.setUserInfoLoadingStatus(true)

                    with(state) {

                        name?.let {
                            userName.text =
                                String.format(getString(R.string.user_name_template), name)
                        } ?: run {
                            userName.text = String.format(
                                getString(R.string.user_name_template),
                                getString(R.string.null_user_name_message)
                            )
                        }

                        bio?.let {
                            userBio.text = String.format(getString(R.string.user_bio_template), bio)
                        } ?: run {
                            userBio.text = String.format(
                                getString(R.string.user_bio_template),
                                getString(R.string.null_user_bio_message)
                            )
                        }

                        userFollowers.text = String.format(
                            getString(R.string.number_of_followers_template),
                            numberOfFollowers.toString()
                        )

                        userRepos.text = String.format(
                            getString(R.string.number_of_repositories_template),
                            numberOfRepositories.toString()
                        )

                        LoadImage.loadImage(
                            this@RepoInfoObtainmentActivity, userImage,
                            R.mipmap.octocat,
                            DiskCacheStrategy.ALL,
                            Priority.IMMEDIATE,
                            DrawableTransitionOptions.withCrossFade(),
                            userAvatar
                        )

                        insertValueIntoSharedPreferencesThroughViewModel(
                            SharedPreferences.githubProfileUrl,
                            state.profileUrl
                        )

                        setViewVisibility(repositoryLoadingProgressBar, View.INVISIBLE)
                    }
                }

                is String -> {
                    setViewVisibility(repositoryLoadingProgressBar, View.INVISIBLE)
                    toastMaker(state)
                }

                else -> {
                    setViewVisibility(repositoryLoadingProgressBar, View.INVISIBLE)
                    toastMaker(getString(R.string.no_connection_error))
                }
            }
        })
    }

    private fun searchProfile() {

        hideKeyboard(searchProfileTextInputEditText)

        if (NetworkChecking.isInternetConnectionAvailable(applicationContext)) {
            if (searchProfileTextInputEditText.text.toString().isNotEmpty()) {
                setViewVisibility(repositoryLoadingProgressBar, View.VISIBLE)
                viewModel.getGithubUserInformation(searchProfileTextInputEditText.text.toString())
            } else toastMaker(getString(R.string.empty_field_error))
        } else toastMaker(getString(R.string.no_connection_error))
    }

    private fun getValueFromSharedPreferencesThroughViewModel(key: String, value: String?) =
        viewModel.getValueFromSharedPreferences(
            SharedPreferencesReference.getSharedPreferencesReference(
                applicationContext, SharedPreferences.sharedPreferencesRoot,
                SharedPreferences.sharedPreferencesPrivateMode
            ), key, value
        )

    private fun insertValueIntoSharedPreferencesThroughViewModel(key: String, value: String) {
        viewModel.insertValueIntoSharedPreferences(
            SharedPreferencesReference.getSharedPreferencesReference(
                applicationContext, SharedPreferences.sharedPreferencesRoot,
                SharedPreferences.sharedPreferencesPrivateMode
            ), key, value
        )
    }

    private fun openRepoPage(url: String?) {
        url?.let {
            startActivity(
                Intent(
                    applicationContext,
                    ShowRepositoryInfoActivity::class.java
                ).putExtra(getString(R.string.repository_opening_intent), url)
            )
        }
    }
}