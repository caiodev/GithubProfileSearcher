package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.repoInformationObtainment.view

import android.content.Intent
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
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.hideKeyboard
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.setViewVisibility
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.toastMaker
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.factory.SharedPreferences
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.imageLoading.LoadImage
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.network.NetworkChecking
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.projectKeys.SharedPreferencesKeys
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.text.TextFormatting.concatenateStrings
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class RepoInfoObtainmentActivity : AppCompatActivity(), ActivityFlow {

    private val viewModel: RepoInfoObtainmentViewModel by lazy {
        ViewModelProviders.of(this).get(RepoInfoObtainmentViewModel::class.java)
    }

    private val sharedPreferences: android.content.SharedPreferences by lazy {
        SharedPreferences.getSharedPreferencesReference(
            applicationContext, SharedPreferencesKeys.sharedPreferencesRoot,
            SharedPreferencesKeys.sharedPreferencesPrivateMode
        )
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

                    viewModel.getValueFromSharedPreferences(
                        sharedPreferences, SharedPreferencesKeys.githubProfileUrl,
                        null
                    )?.let {
                        openRepoPage(it)
                    }
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
                            userName.text = concatenateStrings(
                                getString(R.string.user_name_template),
                                name
                            )
                        } ?: run {
                            userName.text =
                                concatenateStrings(
                                    getString(R.string.user_name_template),
                                    getString(R.string.null_user_name_message)
                                )
                        }

                        bio?.let {
                            userBio.text =
                                concatenateStrings(getString(R.string.user_bio_template), bio)
                        } ?: run {
                            userBio.text = concatenateStrings(
                                getString(R.string.user_bio_template),
                                getString(R.string.null_user_bio_message)
                            )
                        }

                        userFollowers.text = concatenateStrings(
                            getString(R.string.number_of_followers_template),
                            numberOfFollowers.toString()
                        )

                        userRepos.text = concatenateStrings(
                            getString(R.string.number_of_repositories_template),
                            numberOfRepositories.toString()
                        )

                        LoadImage.loadImage(
                            this@RepoInfoObtainmentActivity, userImage,
                            R.mipmap.ic_launcher,
                            DiskCacheStrategy.ALL,
                            Priority.IMMEDIATE,
                            DrawableTransitionOptions.withCrossFade(),
                            userAvatar
                        )

                        Timber.i("VALUE: %s", "Insert: ${state.profileUrl}")

                        viewModel.insertValueIntoSharedPreferences(
                            sharedPreferences, SharedPreferencesKeys.githubProfileUrl,
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

        if (searchProfileTextInputEditText.text.toString().isNotEmpty()) {
            if (NetworkChecking.isInternetConnectionAvailable(applicationContext)) {
                setViewVisibility(repositoryLoadingProgressBar, View.VISIBLE)
                viewModel.getGithubUserInformation(searchProfileTextInputEditText.text.toString())
            } else toastMaker(getString(R.string.no_connection_error))
        } else toastMaker(getString(R.string.empty_field_error))
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