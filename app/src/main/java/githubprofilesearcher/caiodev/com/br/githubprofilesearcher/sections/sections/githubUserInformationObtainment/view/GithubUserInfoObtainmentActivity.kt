package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.adapter.GithubUserAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.viewModel.GithubUserInfoObtainmentViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.showRepositoryInfo.view.ShowRepositoryInfoActivity
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.ActivityFlow
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.hideKeyboard
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.setViewVisibility
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.showSnackBar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.interfaces.OnItemClicked
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.network.NetworkChecking
import kotlinx.android.synthetic.main.activity_main.*

class GithubUserInfoObtainmentActivity : AppCompatActivity(), ActivityFlow {

    private val githubUserAdapter = GithubUserAdapter()

    private val viewModel: GithubUserInfoObtainmentViewModel by lazy {
        ViewModelProviders.of(this).get(GithubUserInfoObtainmentViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        handleViewModel()
    }

    override fun setupView() {

        userInfoRecyclerView.setHasFixedSize(true)
        userInfoRecyclerView.adapter = githubUserAdapter

        searchIconImageView.setOnClickListener {
            searchProfile()
        }

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

            if (noInternetConnectionLinearLayout.isVisible) {
                setViewVisibility(noInternetConnectionLinearLayout, View.INVISIBLE)
            }

            githubUserAdapter.updateDataSource(state)
            runLayoutAnimation(userInfoRecyclerView)
        })

        viewModel.errorStateCopy.observeSingleEvent(this, Observer { state ->

            when (state) {

                is String -> {
                    setViewVisibility(repositoryLoadingProgressBar, View.INVISIBLE)
                    setViewVisibility(githubProfileListSwipeRefreshLayout, View.INVISIBLE)
                    showSnackBar(this, state)
                }

                else -> {
                    setViewVisibility(repositoryLoadingProgressBar, View.INVISIBLE)
                    setViewVisibility(githubProfileListSwipeRefreshLayout, View.INVISIBLE)
                    showOfflineLayout()
                }
            }
        })
    }

    private fun searchProfile() {

        hideKeyboard(searchProfileTextInputEditText)

        if (searchProfileTextInputEditText.text.toString().isNotEmpty()) {

            if (NetworkChecking.isInternetConnectionAvailable(applicationContext)) {
                if (!githubProfileListSwipeRefreshLayout.isRefreshing)
                    setViewVisibility(repositoryLoadingProgressBar, View.VISIBLE)
                viewModel.getGithubUsersList(searchProfileTextInputEditText.text.toString())
            } else showSnackBar(this, getString(R.string.no_connection_error))
        } else showSnackBar(this, getString(R.string.empty_field_error))
    }

    private fun runLayoutAnimation(recyclerView: RecyclerView) {

        recyclerView.layoutAnimation = AnimationUtils.loadLayoutAnimation(
            recyclerView.context, R.anim.layout_animation_fall_down
        )
        recyclerView.adapter?.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
        setViewVisibility(githubProfileListSwipeRefreshLayout)

        if (repositoryLoadingProgressBar.isVisible)
            setViewVisibility(repositoryLoadingProgressBar, View.GONE)
    }

    private fun showOfflineLayout() {

        if (!noInternetConnectionLinearLayout.isVisible)
            setViewVisibility(noInternetConnectionLinearLayout, View.VISIBLE)

        showSnackBar(this, getString(R.string.no_connection_error))
    }
}