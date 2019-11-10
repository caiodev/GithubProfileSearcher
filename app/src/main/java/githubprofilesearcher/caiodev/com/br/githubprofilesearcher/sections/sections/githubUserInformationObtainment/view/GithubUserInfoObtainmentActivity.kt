package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.view

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.adapter.GithubUserAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.repository.GithubUserInformationRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.viewModel.GithubUserInfoObtainmentViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.viewModel.GithubUserInfoObtainmentViewModelFactory
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.showRepositoryInfo.view.ShowRepositoryInfoActivity
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.ActivityFlow
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.cellular
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.disconnected
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.sslHandshakeException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.unknownHostException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.wifi
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.customViews.snackBar.CustomSnackBar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.changeDrawable
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.hideKeyboard
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.setViewVisibility
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.showSnackBar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.interfaces.OnItemClicked
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.network.NetworkChecking
import kotlinx.android.synthetic.main.activity_main.*

class GithubUserInfoObtainmentActivity :
    AppCompatActivity(R.layout.activity_main),
    ActivityFlow {

    private val githubUserAdapter = GithubUserAdapter()
    private var hasUserReachedEndOfList = false
    private var customSnackBar: CustomSnackBar? = null

    private val viewModel: GithubUserInfoObtainmentViewModel by lazy {
        ViewModelProvider(
            this, GithubUserInfoObtainmentViewModelFactory(
                GithubUserInformationRepository()
            )
        ).get(GithubUserInfoObtainmentViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        handleViewModel()
        setupExtras()
    }

    override fun setupView() {

        if (viewModel.isThereAnOngoingCall) {
            setViewVisibility(repositoryLoadingProgressBar, VISIBLE)
            viewModel.shouldActionIconPerformSearch = false
            changeDrawable(actionIconImageView, R.drawable.ic_close)
            setupUpperViewsUserInteraction(false)
        }

        if (viewModel.hasFirstCallBeenMade) changeDrawable(actionIconImageView, R.drawable.ic_close)

        customSnackBar = CustomSnackBar.make(this.findViewById(android.R.id.content))

        userInfoRecyclerView.apply {
            setHasFixedSize(true)
            adapter = githubUserAdapter
            setupRecyclerViewAddOnScrollListener()
        }

        actionIconImageView.setOnClickListener {
            handleActionIconClick()
        }

        setupTextInputEditText()

        githubProfileListSwipeRefreshLayout.setOnRefreshListener {
            viewModel.hasUserRequestedRefresh = true
            searchProfile(isFieldEmpty(), true)
            setupUpperViewsUserInteraction(false)
        }

        githubUserAdapter.setOnItemClicked(object :
            OnItemClicked {

            override fun onItemClick(adapterPosition: Int, id: Int) {
                when (id) {
                    Constants.githubProfileRecyclerViewCell -> {
                        when (NetworkChecking.checkIfInternetConnectionIsAvailable(
                            applicationContext
                        )) {

                            cellular, wifi -> {
                                startActivity(
                                    Intent(
                                        applicationContext,
                                        ShowRepositoryInfoActivity::class.java
                                    )
                                        .putExtra(
                                            Constants.githubProfileUrl,
                                            viewModel.provideProfileUrlThroughViewModel(
                                                adapterPosition
                                            )
                                        )
                                )
                            }

                            disconnected -> showInternetConnectionStatusSnackBar(false)
                        }
                    }
                }
            }
        })
    }

    override fun handleViewModel() {

        //Success LiveData
        viewModel.successMutableLiveData.observe(this, Observer { githubUsersList ->

            setupUpperViewsUserInteraction(true)

            setViewVisibility(githubProfileListSwipeRefreshLayout)

            if (noInternetConnectionLayout.visibility == VISIBLE)
                setViewVisibility(noInternetConnectionLayout, GONE)

            if (viewModel.hasUserRequestedRefresh) {
                setViewVisibility(githubProfileListSwipeRefreshLayout)
                githubUserAdapter.updateDataSource(githubUsersList)
                viewModel.hasUserRequestedRefresh = false
            }

            if (!viewModel.hasFirstCallBeenMade) {
                githubUserAdapter.updateDataSource(githubUsersList)
            } else {
                githubUserAdapter.updateDataSource(githubUsersList)
                userInfoRecyclerView.adapter?.notifyDataSetChanged()
                setViewVisibility(repositoryLoadingProgressBar, GONE)
            }
            hasUserReachedEndOfList = false
            runLayoutAnimation(userInfoRecyclerView)
        })

        //Error LiveData
        viewModel.errorSingleLiveEvent.observeSingleEvent(this, Observer { state ->

            viewModel.shouldActionIconPerformSearch = true
            setupUpperViewsUserInteraction(true)
            changeDrawable(actionIconImageView, R.drawable.ic_search)

            when (state) {
                unknownHostException, sslHandshakeException -> {
                    if (viewModel.hasFirstCallBeenMade)
                        showErrorMessages(state, false)
                    else showErrorMessages(state, true)
                }

                else -> showErrorMessages(state, false)
            }
        })
    }

    override fun setupExtras() {

        when (NetworkChecking.checkIfInternetConnectionIsAvailable(applicationContext)) {
            disconnected -> showInternetConnectionStatusSnackBar(false)
        }

        setupInternetConnectionObserver()
    }

    private fun searchProfile(isFieldEmpty: Boolean, shouldTheListItemsBeRemoved: Boolean? = null) {

        hideKeyboard(searchProfileTextInputEditText)

        if (!isFieldEmpty) {
            callApi {
                viewModel.getGithubUsersList(
                    searchProfileTextInputEditText.text.toString(),
                    shouldTheListItemsBeRemoved
                )
            }
            viewModel.isThereAnOngoingCall = true
        } else {
            showSnackBar(
                this,
                getString(R.string.empty_field_error)
            )

            viewModel.hasUserRequestedRefresh = false
        }
    }

    private fun runLayoutAnimation(recyclerView: RecyclerView) {
        with(recyclerView) {
            layoutAnimation = AnimationUtils.loadLayoutAnimation(
                context,
                R.anim.layout_animation_fall_down
            )
            adapter?.notifyDataSetChanged()
            scheduleLayoutAnimation()
            viewModel.hasFirstCallBeenMade = true

            setViewVisibility(githubProfileListSwipeRefreshLayout)

            if (repositoryLoadingProgressBar.visibility == VISIBLE)
                setViewVisibility(repositoryLoadingProgressBar, GONE)
        }
    }

    private fun callApi(genericFunction: () -> Unit) {

        when (NetworkChecking.checkIfInternetConnectionIsAvailable(applicationContext)) {

            cellular, wifi -> {
                if (!githubProfileListSwipeRefreshLayout.isRefreshing)
                    setViewVisibility(repositoryLoadingProgressBar, VISIBLE)
                genericFunction.invoke()
            }

            disconnected -> showInternetConnectionStatusSnackBar(false)
        }
    }

    private fun showErrorMessages(message: Int, shouldOfflineLayoutBeShown: Boolean) {
        if (shouldOfflineLayoutBeShown) {
            if (noInternetConnectionLayout.visibility != VISIBLE)
                setViewVisibility(noInternetConnectionLayout, VISIBLE)
        }
        setViewVisibility(repositoryLoadingProgressBar, GONE)
        setViewVisibility(githubProfileListSwipeRefreshLayout)
        showSnackBar(this, getString(message))
    }

    private fun isFieldEmpty() = searchProfileTextInputEditText.text.toString().isEmpty()

    private fun setupInternetConnectionObserver() {
        NetworkChecking.internetConnectionAvailabilityObservable(applicationContext)
            .observe(this, Observer { isInternetAvailable ->
                when (isInternetAvailable) {
                    true -> showInternetConnectionStatusSnackBar(true)
                    false -> showInternetConnectionStatusSnackBar(false)
                }
            })
    }

    private fun showInternetConnectionStatusSnackBar(isInternetConnectionAvailable: Boolean) {
        with(customSnackBar) {
            if (isInternetConnectionAvailable) {
                this?.setText(getString(R.string.back_online_success_message))?.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.green_700
                    )
                )
                this?.dismiss()
            } else {
                this?.setText(getString(R.string.no_connection_error))?.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.red_700
                    )
                )
                this?.show()
            }
        }
    }

    private fun handleActionIconClick() {
        if (!viewModel.isThereAnOngoingCall) {
            if (viewModel.shouldActionIconPerformSearch) {
                if (!isFieldEmpty()) {
                    searchProfile(isFieldEmpty = false, shouldTheListItemsBeRemoved = true)
                    setupUpperViewsUserInteraction(false)
                    changeDrawable(actionIconImageView, R.drawable.ic_close)
                    viewModel.shouldActionIconPerformSearch = false
                } else searchProfile(isFieldEmpty = true, shouldTheListItemsBeRemoved = true)
            } else {
                searchProfileTextInputEditText.setText("")
                changeDrawable(actionIconImageView, R.drawable.ic_search)
                viewModel.shouldActionIconPerformSearch = true
            }
        }
    }

    private fun setupTextInputEditText() {
        with(searchProfileTextInputEditText) {
            requestFocus()
            setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    setupUpperViewsUserInteraction(false)
                    viewModel.shouldActionIconPerformSearch = false
                    changeDrawable(actionIconImageView, R.drawable.ic_close)
                    searchProfile(isFieldEmpty(), true)
                    return@OnEditorActionListener true
                }
                false
            })

            addTextChangedListener {
                doOnTextChanged { text, _, _, _ ->
                    viewModel.shouldActionIconPerformSearch = true
                    if (text.isNullOrEmpty()) changeDrawable(
                        actionIconImageView,
                        R.drawable.ic_search
                    )
                }
            }
        }
    }

    private fun setupRecyclerViewAddOnScrollListener() {
        userInfoRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val total = recyclerView.layoutManager?.itemCount
                val currentLastItem =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                if (currentLastItem == total?.minus(1)) {
                    if (!isFieldEmpty()) {
                        if (!hasUserReachedEndOfList) {
                            searchProfile(
                                isFieldEmpty = false,
                                shouldTheListItemsBeRemoved = false
                            )
                            hasUserReachedEndOfList = true
                        }
                    } else searchProfile(isFieldEmpty = true)
                }
            }
        })
    }

    private fun setupUpperViewsUserInteraction(shouldUsersBeAbleToInteractWithTheUpperViews: Boolean) {

        actionIconImageView.apply {
            isClickable = shouldUsersBeAbleToInteractWithTheUpperViews
            isFocusable = shouldUsersBeAbleToInteractWithTheUpperViews
        }

        with(searchProfileTextInputEditText) {
            isClickable = shouldUsersBeAbleToInteractWithTheUpperViews
            isCursorVisible = shouldUsersBeAbleToInteractWithTheUpperViews
            isFocusable = shouldUsersBeAbleToInteractWithTheUpperViews
            isFocusableInTouchMode = shouldUsersBeAbleToInteractWithTheUpperViews
            isLongClickable = shouldUsersBeAbleToInteractWithTheUpperViews
            if (shouldUsersBeAbleToInteractWithTheUpperViews) requestFocus()
        }
    }
}