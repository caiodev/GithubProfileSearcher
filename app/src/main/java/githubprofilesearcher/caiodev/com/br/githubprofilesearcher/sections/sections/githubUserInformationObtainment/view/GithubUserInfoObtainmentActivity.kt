package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.view

import android.content.Intent
import android.os.Bundle
import android.view.View.*
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.adapter.GithubUserAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.viewModel.GithubUserInfoObtainmentViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.showRepositoryInfo.view.ShowRepositoryInfoActivity
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.ActivityFlow
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.sslHandshakeException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.unknownHostException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.hideKeyboard
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.setViewVisibility
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.showSnackBar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.interfaces.OnItemClicked
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.network.NetworkChecking
import kotlinx.android.synthetic.main.activity_main.*

class GithubUserInfoObtainmentActivity : AppCompatActivity(R.layout.activity_main), ActivityFlow {

    private val githubUserAdapter = GithubUserAdapter()

    private val viewModel: GithubUserInfoObtainmentViewModel by lazy {
        ViewModelProvider(this).get(GithubUserInfoObtainmentViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        handleViewModel()
    }

    override fun setupView() {

        userInfoRecyclerView.setHasFixedSize(true)
        userInfoRecyclerView.adapter = githubUserAdapter

        searchIconImageView.setOnClickListener {
            searchProfile()
        }

        searchProfileTextInputEditText.requestFocus()

        searchProfileTextInputEditText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchProfile()
                return@OnEditorActionListener true
            }
            false
        })

        githubProfileListSwipeRefreshLayout.setOnRefreshListener {
            searchProfile()
        }

        githubUserAdapter.setOnItemClicked(object :
            OnItemClicked {

            override fun onItemClick(adapterPosition: Int, id: Int) {

                when (id) {

                    Constants.githubProfileRecyclerViewCell -> {

                        if (!NetworkChecking.isInternetConnectionAvailable(applicationContext)) {
                            showSnackBar(
                                this@GithubUserInfoObtainmentActivity,
                                getString(R.string.no_connection_error)
                            )
                        } else {
                            startActivity(
                                Intent(applicationContext, ShowRepositoryInfoActivity::class.java)
                                    .putExtra(
                                        Constants.githubProfileUrl,
                                        viewModel.getProfileUrlThroughViewModel(adapterPosition)
                                    )
                            )
                        }
                    }
                }
            }
        })
    }

    override fun handleViewModel() {

        viewModel.successStateCopy.observe(this, Observer { state ->
            if (noInternetConnectionLayout.visibility == VISIBLE)
                setViewVisibility(noInternetConnectionLayout, INVISIBLE)
            githubUserAdapter.updateDataSource(state)
            runLayoutAnimation(userInfoRecyclerView)
        })

        viewModel.errorStateCopy.observeSingleEvent(this, Observer { state ->
            when (state as Int) {
                unknownHostException, sslHandshakeException -> showErrorMessages(state, true)
                else -> showErrorMessages(state, false)
            }
        })
    }

    private fun searchProfile() {

        hideKeyboard(searchProfileTextInputEditText)

        if (searchProfileTextInputEditText.text.toString().isNotEmpty()) {
            callApi { viewModel.getGithubUsersList(searchProfileTextInputEditText.text.toString()) }
        } else showSnackBar(this, getString(R.string.empty_field_error))
    }

    private fun runLayoutAnimation(recyclerView: RecyclerView) {

        recyclerView.layoutAnimation = AnimationUtils.loadLayoutAnimation(
            recyclerView.context, R.anim.layout_animation_fall_down
        )
        recyclerView.adapter?.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
        setViewVisibility(githubProfileListSwipeRefreshLayout)

        if (repositoryLoadingProgressBar.visibility == VISIBLE)
            setViewVisibility(repositoryLoadingProgressBar, GONE)
    }

    private fun callApi(genericFunction: () -> Unit) {
        if (NetworkChecking.isInternetConnectionAvailable(applicationContext)) {
            if (!githubProfileListSwipeRefreshLayout.isRefreshing)
                setViewVisibility(repositoryLoadingProgressBar, VISIBLE)
            genericFunction.invoke()
        } else showSnackBar(this, getString(R.string.no_connection_error))
    }

    private fun showErrorMessages(message: Int, shouldOfflineLayoutBeShown: Boolean) {
        if (shouldOfflineLayoutBeShown) {
            if (noInternetConnectionLayout.visibility != VISIBLE)
                setViewVisibility(noInternetConnectionLayout, VISIBLE)
        }
        setViewVisibility(repositoryLoadingProgressBar, INVISIBLE)
        setViewVisibility(githubProfileListSwipeRefreshLayout, INVISIBLE)
        showSnackBar(this, getString(message))
    }
}