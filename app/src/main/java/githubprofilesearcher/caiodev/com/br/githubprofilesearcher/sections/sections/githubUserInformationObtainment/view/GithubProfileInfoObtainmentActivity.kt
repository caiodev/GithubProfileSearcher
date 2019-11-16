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
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.adapter.GithubProfileAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.repository.GithubProfileInformationRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.viewModel.GithubProfileInfoObtainmentViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.viewModel.GithubProfileInfoObtainmentViewModelFactory
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.showRepositoryInfo.view.ShowRepositoryInfoActivity
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.ActivityFlow
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.cellular
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.disconnected
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.forbidden
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
import kotlinx.android.synthetic.main.no_internet_connection.view.*

class GithubProfileInfoObtainmentActivity :
    AppCompatActivity(R.layout.activity_main),
    ActivityFlow {

    private val githubUserAdapter = GithubProfileAdapter()
    private var customSnackBar: CustomSnackBar? = null
    private var hasUserRequestedAnotherResultPage = false
    private var shouldRecyclerViewAnimationBeExecuted = true

    private val viewModel: GithubProfileInfoObtainmentViewModel by lazy {
        ViewModelProvider(
            this, GithubProfileInfoObtainmentViewModelFactory(
                GithubProfileInformationRepository()
            )
        ).get(GithubProfileInfoObtainmentViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        handleViewModel()
        setupExtras()
    }

    override fun setupView() {

        //Condition when users rotate the screen and the activity gets destroyed
        if (viewModel.isThereAnOngoingCall) {
            setViewVisibility(repositoryLoadingProgressBar, VISIBLE)
            setupUpperViewsUserInteraction(false)
            changeDrawable(actionIconImageView, R.drawable.ic_close)
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
            viewModel.hasUserRequestedAListRefresh = true
            searchProfile(null, true)
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

        noInternetConnectionLayout.retryButton.setOnClickListener {
            if (!isFieldEmpty())
                searchProfile(isFieldEmpty = false, shouldTheListItemsBeRemoved = true)
            else
                searchProfile(isFieldEmpty = true, shouldTheListItemsBeRemoved = true)
        }
    }

    override fun handleViewModel() {

        //Success LiveData
        viewModel.successMutableLiveData.observe(this, Observer { githubUsersList ->

            setupUpperViewsUserInteraction(true)
            setViewVisibility(githubProfileListSwipeRefreshLayout)

            if (noInternetConnectionLayout.visibility == VISIBLE)
                setViewVisibility(noInternetConnectionLayout, GONE)

            if (viewModel.hasUserRequestedAListRefresh) {
                setViewVisibility(githubProfileListSwipeRefreshLayout)
                githubUserAdapter.updateDataSource(githubUsersList)
                viewModel.hasUserRequestedAListRefresh = false
            }

            if (!viewModel.hasFirstCallBeenMade)
                githubUserAdapter.updateDataSource(githubUsersList)
            else {
                githubUserAdapter.updateDataSource(githubUsersList)
                userInfoRecyclerView.adapter?.notifyDataSetChanged()
                setViewVisibility(repositoryLoadingProgressBar, GONE)
            }

            if (shouldRecyclerViewAnimationBeExecuted)
                runLayoutAnimation(userInfoRecyclerView)

            if (!shouldRecyclerViewAnimationBeExecuted)
                shouldRecyclerViewAnimationBeExecuted = true
        })

        //Error LiveData
        viewModel.errorSingleLiveEvent.observeSingleEvent(this, Observer { state ->

            if (!shouldRecyclerViewAnimationBeExecuted) shouldRecyclerViewAnimationBeExecuted = true
            setupUpperViewsUserInteraction(true)
            if (state.first != forbidden) changeDrawable(actionIconImageView, R.drawable.ic_search)

            when (state.first) {
                unknownHostException -> {
                    if (viewModel.hasFirstCallBeenMade)
                        showErrorMessages(state.second, false)
                    else showErrorMessages(state.second, true)
                }

                else -> showErrorMessages(state.second, false)
            }
        })
    }

    override fun setupExtras() {

        when (NetworkChecking.checkIfInternetConnectionIsAvailable(applicationContext)) {
            disconnected -> showInternetConnectionStatusSnackBar(false)
        }

        setupInternetConnectionObserver()
    }

    private fun searchProfile(
        isFieldEmpty: Boolean? = null,
        shouldTheListItemsBeRemoved: Boolean? = null
    ) {

        hideKeyboard(searchProfileTextInputEditText)

        //Since the SwipeRefreshLayout is only visible when at least one call has already been made, even if users delete the text from the
        //TextInputEditText, users will already have typed something onto it and this string will be stored in the ViewModel as soon as they search for it, so,
        //it can be used to either refresh the list or to keep getting more pages from the Github API. It does not apply to the "ActionIcon" when it is a Magnifying glass icon
        // or when users search using the Keyboard Magnifying Glass icon, in this case, there has to be some text on the TextInputEditText
        if (viewModel.hasUserRequestedAListRefresh) {
            callApi {
                viewModel.getGithubProfileList(
                    null,
                    shouldTheListItemsBeRemoved
                )
            }
        }

        if (hasUserRequestedAnotherResultPage) {
            callApi {
                viewModel.getGithubProfileList(
                    null,
                    shouldTheListItemsBeRemoved
                )
            }
        }

        isFieldEmpty?.let {
            if (!it) {
                callApi {
                    viewModel.getGithubProfileList(
                        searchProfileTextInputEditText.text.toString(),
                        shouldTheListItemsBeRemoved
                    )
                }
            } else {
                showSnackBar(
                    this,
                    getString(R.string.empty_field_error)
                )

                viewModel.hasUserRequestedAListRefresh = false
            }
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
                setupUpperViewsUserInteraction(false)
                changeDrawable(actionIconImageView, R.drawable.ic_close)
                genericFunction.invoke()
                viewModel.isThereAnOngoingCall = true
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
                    if (!isFieldEmpty()) {
                        searchProfile(isFieldEmpty = false, shouldTheListItemsBeRemoved = true)
                    }
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
                    //This attribute was created to avoid making an API call twice or more because sometimes this callback is called more than once, so,
                    //the API call method won't be called until the previous API call finishes combining it with the 'isThereAnOngoingCall' attribute located in the
                    //GithubProfileInfoObtainmentViewModel
                    hasUserRequestedAnotherResultPage = true
                    shouldRecyclerViewAnimationBeExecuted = false
                    if (hasUserRequestedAnotherResultPage && !viewModel.isThereAnOngoingCall && !viewModel.hasUserRequestedAListRefresh) {
                        searchProfile(
                            isFieldEmpty = null,
                            shouldTheListItemsBeRemoved = false
                        )
                    }
                }
            }
        })
    }

    private fun setupUpperViewsUserInteraction(shouldUsersBeAbleToInteractWithTheUpperViews: Boolean) {

        viewModel.shouldActionIconPerformSearch = shouldUsersBeAbleToInteractWithTheUpperViews

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