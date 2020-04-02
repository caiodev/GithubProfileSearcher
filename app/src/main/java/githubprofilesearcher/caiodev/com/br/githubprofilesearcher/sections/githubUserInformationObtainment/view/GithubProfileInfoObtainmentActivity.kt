package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View.*
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.idling.CountingIdlingResource
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.adapter.GithubProfileAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel.GithubProfileViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.showUserRepositoryInformation.view.ShowRepositoryInfoActivity
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.ActivityFlow
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.githubProfileCell
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.retry
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.customViews.snackBar.CustomSnackBar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.*
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.interfaces.OnItemClicked
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.network.NetworkChecking.checkIfInternetConnectionIsAvailable
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.network.NetworkChecking.internetConnectionAvailabilityObservable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.serialization.UnstableDefault
import org.koin.androidx.viewmodel.ext.android.viewModel

class GithubProfileInfoObtainmentActivity :
    AppCompatActivity(R.layout.activity_main),
    ActivityFlow {

    private var shouldRecyclerViewAnimationBeExecuted = true
    private var hasBackToTopButtonBeenClicked = false

    private lateinit var countingIdlingResource: CountingIdlingResource

    private lateinit var customSnackBar: CustomSnackBar

    private val viewModel by viewModel<GithubProfileViewModel>()

    private val githubUserAdapter by lazy {
        GithubProfileAdapter()
    }

    @UnstableDefault
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setupView()
        handleViewModel()
        setupExtras()
    }

    @UnstableDefault
    override fun setupView() {
        setupDarkMode()
        bindViewModelDataToUIInCaseOfOrientationChanges()
        setupCustomSnackBar()
        setupImageViews()
        setupSwipeRefreshLayout()
        setupRecyclerView()
        setupTextInputEditText()
        adapterCallback()
    }

    private fun bindViewModelDataToUIInCaseOfOrientationChanges() {
        //Condition when users rotate the screen and the activity gets destroyed
        if (viewModel.flags.isThereAnOngoingCall) {
            applyViewVisibility(repositoryLoadingProgressBar, VISIBLE)
            setupUpperViewsInteraction(false)
            viewModel.flags.shouldASearchBePerformed = false
            changeDrawable(actionIconImageView, R.drawable.ic_close)
        }

        if (viewModel.flags.hasFirstSuccessfulCallBeenMade && !viewModel.flags.isThereAnyProfileToBeSearched) {
            changeDrawable(
                actionIconImageView,
                R.drawable.ic_close
            )
        }

        if (viewModel.flags.lastVisibleItem >= 10) applyViewVisibility(backToTopButton, VISIBLE)
    }

    private fun setupDarkMode() {

        //Setting true black because in my case what was applied way actually a dark grey shade
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                applyBackgroundColor(
                    parentConstraintLayout,
                    android.R.color.black
                )
            }
        }
    }

    private fun setupCustomSnackBar() {
        customSnackBar = CustomSnackBar.make(this.findViewById(android.R.id.content))
    }

    @UnstableDefault
    private fun setupImageViews() {

        actionIconImageView.setOnClickListener {
            handleActionIconClick()
        }

        backToTopButton.setOnClickListener {
            if (backToTopButton.visibility != GONE) applyViewVisibility(
                backToTopButton,
                INVISIBLE
            )
            hasBackToTopButtonBeenClicked = true
            scrollToTop(true)
        }
    }

    @UnstableDefault
    private fun setupSwipeRefreshLayout() {
        githubProfileListSwipeRefreshLayout.setOnRefreshListener {
            viewModel.flags.hasAnyUserRequestedUpdatedData = true
            viewModel.flags.hasUserTriggeredANewRequest = true
            swipeRefreshCall()
        }
    }

    @UnstableDefault
    private fun setupRecyclerView() {
        profileInfoRecyclerView.apply {
            setHasFixedSize(true)
            adapter = githubUserAdapter
            setupRecyclerViewAddOnScrollListener()
        }
    }

    @UnstableDefault
    private fun setupTextInputEditText() {
        with(searchProfileTextInputEditText) {
            setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    viewModel.flags.hasUserTriggeredANewRequest = true
                    textInputEditTextNotEmptyRequiredCall()
                    return@OnEditorActionListener true
                }
                false
            })

            addTextChangedListener {
                doOnTextChanged { _, _, _, _ ->
                    if (!viewModel.flags.shouldASearchBePerformed) viewModel.flags.shouldASearchBePerformed =
                        true
                    changeDrawable(
                        actionIconImageView,
                        R.drawable.ic_search
                    )
                }
            }
        }
    }

    @UnstableDefault
    private fun adapterCallback() {

        githubUserAdapter.setOnItemClicked(object : OnItemClicked {

            override fun onItemClick(adapterPosition: Int, id: Int) {

                when (id) {
                    githubProfileCell -> {
                        checkIfInternetConnectionIsAvailableCaller(
                            onConnectionAvailable = {
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
                            },
                            onConnectionUnavailable = { showInternetConnectionStatusSnackBar(false) })
                    }

                    retry -> paginationCall()
                }
            }
        })
    }

    override fun handleViewModel() {
        onSuccess()
        onError()
    }

    private fun onSuccess() {

        viewModel.mainListLiveData.observe(this) { githubUsersList ->

            if (this::countingIdlingResource.isInitialized)
                countingIdlingResource.decrement()

            setupUpperViewsInteraction(true)
            viewModel.flags.shouldASearchBePerformed = false
            applyViewVisibility(githubProfileListSwipeRefreshLayout)

            shouldRecyclerViewAnimationBeExecuted =
                if (!viewModel.flags.hasFirstSuccessfulCallBeenMade || viewModel.flags.hasUserTriggeredANewRequest) {
                    githubUserAdapter.updateDataSource(githubUsersList)
                    true
                } else {
                    githubUserAdapter.updateDataSource(githubUsersList)
                    profileInfoRecyclerView.adapter?.notifyDataSetChanged()
                    applyViewVisibility(repositoryLoadingProgressBar, GONE)
                    false
                }

            if (viewModel.flags.hasUserTriggeredANewRequest) viewModel.flags.hasUserTriggeredANewRequest = false

            if (viewModel.flags.hasAnyUserRequestedUpdatedData) {
                applyViewVisibility(githubProfileListSwipeRefreshLayout)
                githubUserAdapter.updateDataSource(githubUsersList)
                shouldRecyclerViewAnimationBeExecuted = true
                viewModel.flags.hasAnyUserRequestedUpdatedData = false
            }

            if (shouldRecyclerViewAnimationBeExecuted)
                runLayoutAnimation(profileInfoRecyclerView)
            else
                shouldRecyclerViewAnimationBeExecuted = true
        }
    }

    private fun onError() {

        viewModel.errorSingleImmutableLiveDataEvent.observe(this) { error ->

            if (this::countingIdlingResource.isInitialized)
                countingIdlingResource.decrement()

            if (viewModel.flags.hasUserTriggeredANewRequest) viewModel.flags.hasUserTriggeredANewRequest = false

            if (!shouldRecyclerViewAnimationBeExecuted)
                shouldRecyclerViewAnimationBeExecuted = true

            setupUpperViewsInteraction(true)

            viewModel.flags.shouldASearchBePerformed = true
            changeDrawable(actionIconImageView, R.drawable.ic_search)
            showErrorMessages(error)
        }
    }

    @UnstableDefault
    override fun setupExtras() {
        checkIfInternetConnectionIsAvailableCaller(
            onConnectionAvailable = {},
            onConnectionUnavailable = { showInternetConnectionStatusSnackBar(false) })
        setupInternetConnectionObserver()
    }

    @UnstableDefault
    private fun swipeRefreshCall() {
        callApiThroughViewModel { viewModel.requestUpdatedGithubProfiles() }
    }

    @UnstableDefault
    private fun paginationCall() {
        viewModel.requestMoreGithubProfiles()
    }

    @UnstableDefault
    private fun textInputEditTextNotEmptyRequiredCall() {
        hideKeyboard(searchProfileTextInputEditText)
        if (!isTextInputEditTextEmpty()) {
            applyViewVisibility(repositoryLoadingProgressBar, VISIBLE)
            viewModel.flags.hasUserTriggeredANewRequest = true
            callApiThroughViewModel {
                viewModel.requestUpdatedGithubProfiles(
                    searchProfileTextInputEditText.text.toString()
                )
            }
        } else {
            showSnackBar(
                this,
                getString(R.string.empty_field_error)
            ) {
                checkIfInternetConnectionIsAvailableCaller({},
                    { showInternetConnectionStatusSnackBar(false) })
            }
            viewModel.flags.hasAnyUserRequestedUpdatedData = false
        }
    }

    private fun runLayoutAnimation(recyclerView: RecyclerView) {
        with(recyclerView) {
            layoutAnimation = AnimationUtils.loadLayoutAnimation(
                context,
                R.anim.layout_animation_fall_down
            )
            adapter?.notifyDataSetChanged()

            // For some reason, when a call that removes all previous items from the result list is made
            // (e.g: Pull-to-refresh or by clicking on the search icon), if one scrolled till some point of the list,
            // when the call is finished, the top of the list is not shown, which should happen. So a temporary solution
            // until i figure out what is going on is to go back to the top after any successful call that is required to
            // remove all previous list items so pagination call will not be affected by this
            scrollToTop(false)
            scheduleLayoutAnimation()
            applyViewVisibility(githubProfileListSwipeRefreshLayout)

            if (repositoryLoadingProgressBar.visibility == VISIBLE)
                applyViewVisibility(repositoryLoadingProgressBar, GONE)
        }
    }

    private inline fun callApiThroughViewModel(crossinline genericFunction: () -> Unit) {
        checkIfInternetConnectionIsAvailableCaller(
            onConnectionAvailable = {
                setupUpperViewsInteraction(false)
                viewModel.flags.shouldASearchBePerformed = false
                changeDrawable(actionIconImageView, R.drawable.ic_close)
                genericFunction.invoke()
                if (this::countingIdlingResource.isInitialized)
                    countingIdlingResource.increment()
            },
            onConnectionUnavailable = {
                showErrorMessages(R.string.no_connection_error)
            })
    }

    private fun showErrorMessages(message: Int) {
        hideKeyboard(searchProfileTextInputEditText)
        applyViewVisibility(repositoryLoadingProgressBar, GONE)
        //SwipeRefreshLayout will only be visible if at least one successful call has been made so it will only be called if such condition is met
        if (viewModel.flags.hasFirstSuccessfulCallBeenMade) applyViewVisibility(
            githubProfileListSwipeRefreshLayout
        )
        setupUpperViewsInteraction(true)
        showSnackBar(
            this,
            getString(message),
            onDismissed = {
                checkIfInternetConnectionIsAvailableCaller(
                    {},
                    { showInternetConnectionStatusSnackBar(false) })
            })
    }

    private fun checkIfInternetConnectionIsAvailableCaller(
        onConnectionAvailable: () -> Unit,
        onConnectionUnavailable: () -> Unit
    ) {
        checkIfInternetConnectionIsAvailable(
            applicationContext,
            onConnectionAvailable,
            onConnectionUnavailable
        )
    }

    @UnstableDefault
    private fun setupInternetConnectionObserver() {
        internetConnectionAvailabilityObservable(applicationContext)
            .observe(this) { isInternetAvailable ->
                when (isInternetAvailable) {
                    true -> {
                        showInternetConnectionStatusSnackBar(true)
                        if (viewModel.flags.hasFirstSuccessfulCallBeenMade) {
                            if (!viewModel.flags.isThereAnOngoingCall && viewModel.flags.isRetryItemVisible) {
                                paginationCall()
                            }
                        } else
                            if (!isTextInputEditTextEmpty() && !viewModel.flags.isThereAnOngoingCall) {
                                textInputEditTextNotEmptyRequiredCall()
                            }
                    }
                    false -> showInternetConnectionStatusSnackBar(false)
                }
            }
    }

    private fun showInternetConnectionStatusSnackBar(isInternetConnectionAvailable: Boolean) {
        with(customSnackBar) {
            if (isInternetConnectionAvailable) {
                setText(getString(R.string.back_online_success_message)).setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.green_700
                    )
                )
                if (isShown) dismiss()
            } else {
                setText(getString(R.string.no_connection_error)).setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.red_700
                    )
                )
                show()
            }
        }
    }

    @UnstableDefault
    private fun handleActionIconClick() {
        if (!viewModel.flags.isThereAnOngoingCall) {
            if (viewModel.flags.shouldASearchBePerformed) {
                textInputEditTextNotEmptyRequiredCall()
            } else {
                searchProfileTextInputEditText.setText("")
                changeDrawable(actionIconImageView, R.drawable.ic_search)
                if (!viewModel.flags.shouldASearchBePerformed) viewModel.flags.shouldASearchBePerformed = true
            }
        }
    }

    @UnstableDefault
    private fun setupRecyclerViewAddOnScrollListener() {
        profileInfoRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val total = recyclerView.layoutManager?.itemCount
                val currentLastItem =
                    ((recyclerView.layoutManager) as LinearLayoutManager).findLastVisibleItemPosition()

                viewModel.flags.lastVisibleItem = currentLastItem

                if (currentLastItem in 4..9)
                    if (backToTopButton.visibility != GONE) {
                        applyViewVisibility(backToTopButton, GONE)
                        hasBackToTopButtonBeenClicked = false
                    }

                if (currentLastItem >= 10 && backToTopButton.visibility != VISIBLE && !hasBackToTopButtonBeenClicked) {
                    applyViewVisibility(
                        backToTopButton,
                        VISIBLE
                    )
                }

                if (currentLastItem == total?.minus(1) && !viewModel.flags.isTheNumberOfItemsOfTheLastCallLessThanTwenty) {
                    /* This attribute was created to avoid  making an API call twice or more because sometimes this callback is called more than once, so,
                    the API call method won't be called until the previous API call finishes combining it with the 'isThereAnOngoingCall' attribute located in the
                    GithubProfileViewModel */
                    shouldRecyclerViewAnimationBeExecuted = false
                    if (!viewModel.flags.isThereAnOngoingCall && !viewModel.flags.isRetryItemVisible) {
                        paginationCall()
                    }
                }
            }
        })
    }

    private fun setupUpperViewsInteraction(shouldUsersBeAbleToInteractWithTheUpperViews: Boolean) {

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

    private fun scrollToTop(shouldScrollBeSmooth: Boolean) {
        if (shouldScrollBeSmooth)
            profileInfoRecyclerView.smoothScrollToPosition(0)
        else
            profileInfoRecyclerView.scrollToPosition(0)
    }

    private fun isTextInputEditTextEmpty() =
        searchProfileTextInputEditText.text.toString().isEmpty()

    fun bindIdlingResource(receivedCountingIdlingResource: CountingIdlingResource) {
        countingIdlingResource = receivedCountingIdlingResource
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.flags.isThereAnyProfileToBeSearched =
            isTextInputEditTextEmpty()
    }
}