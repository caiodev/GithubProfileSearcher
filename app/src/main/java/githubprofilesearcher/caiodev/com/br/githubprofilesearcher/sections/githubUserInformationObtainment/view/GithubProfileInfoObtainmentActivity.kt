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
import androidx.recyclerview.widget.MergeAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.idling.CountingIdlingResource
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.adapter.GithubProfileAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.adapter.HeaderAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.adapter.TransientViewsAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel.GithubProfileViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.showUserRepositoryInformation.view.ShowRepositoryInfoActivity
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.ActivityFlow
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.hasAnyUserRequestedUpdatedData
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.hasFirstSuccessfulCallBeenMade
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.hasUserTriggeredANewRequest
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.header
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.isRetryItemVisible
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.isThereAnOngoingCall
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.isThereAnyProfileToBeSearched
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.shouldASearchBePerformed
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

    private lateinit var countingIdlingResource: CountingIdlingResource
    private lateinit var customSnackBar: CustomSnackBar
    private val viewModel by viewModel<GithubProfileViewModel>()

    private val mergeAdapter by lazy {
        MergeAdapter(
            HeaderAdapter(R.string.github_user_list_header),
            GithubProfileAdapter(),
            TransientViewsAdapter()
        )
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
        if (viewModel.provideStateValue(isThereAnOngoingCall)) {
            applyViewVisibility(repositoryLoadingProgressBar, VISIBLE)
            setupUpperViewsInteraction(false)
            viewModel.saveStateValue(shouldASearchBePerformed, false)
            changeDrawable(actionIconImageView, R.drawable.ic_close)
        }

        if (viewModel.provideStateValue(hasFirstSuccessfulCallBeenMade) && !viewModel.provideStateValue<Boolean>(
                isThereAnyProfileToBeSearched
            )
        ) {
            changeDrawable(
                actionIconImageView,
                R.drawable.ic_close
            )
        }

        if (provideRecyclerViewLayoutManager().findFirstVisibleItemPosition() >= 2) {
            applyViewVisibility(backToTopButton, VISIBLE)
        }
    }

    private fun setupDarkMode() {

        //Setting true black because in my case what was applied way actually a dark grey shade
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES ->
                applyBackgroundColor(
                    parentConstraintLayout,
                    android.R.color.black
                )
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
            backToTopButton.isClickable = false
            applyViewVisibility(backToTopButton, INVISIBLE)
            scrollToTop(true)
        }
    }

    @UnstableDefault
    private fun setupSwipeRefreshLayout() {
        githubProfileListSwipeRefreshLayout.apply {
            isEnabled = false
            isRefreshing = false
            setOnRefreshListener {
                viewModel.saveStateValue(hasAnyUserRequestedUpdatedData, true)
                viewModel.saveStateValue(hasUserTriggeredANewRequest, true)
                swipeRefreshCall()
            }
        }
    }

    @UnstableDefault
    private fun setupRecyclerView() {
        profileInfoRecyclerView.apply {
            setHasFixedSize(true)
            adapter = mergeAdapter
            setupRecyclerViewAddOnScrollListener()
        }
    }

    @UnstableDefault
    private fun setupTextInputEditText() {
        with(searchProfileTextInputEditText) {
            setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    viewModel.saveStateValue(hasUserTriggeredANewRequest, true)
                    textInputEditTextNotEmptyRequiredCall()
                    return@OnEditorActionListener true
                }
                false
            })

            addTextChangedListener {
                doOnTextChanged { _, _, _, _ ->
                    if (!viewModel.provideStateValue<Boolean>(shouldASearchBePerformed)) viewModel.saveStateValue(
                        shouldASearchBePerformed,
                        true
                    )

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

        (mergeAdapter.adapters[1] as GithubProfileAdapter).setOnItemClicked(object : OnItemClicked {

            override fun onItemClick(adapterPosition: Int, id: Int) {

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
        })

        (mergeAdapter.adapters[2] as TransientViewsAdapter).setOnItemClicked(object :
            OnItemClicked {
            override fun onItemClick(adapterPosition: Int, id: Int) {
                paginationCall()
            }
        })
    }

    override fun handleViewModel() {
        onSuccess()
        onError()
    }

    private fun onSuccess() {

        viewModel.successLiveData.observe(this) { githubUsersList ->

            if (this::countingIdlingResource.isInitialized)
                countingIdlingResource.decrement()

            setupUpperViewsInteraction(true)
            viewModel.saveStateValue(shouldASearchBePerformed, false)

            (mergeAdapter.adapters[1] as GithubProfileAdapter).apply {
                updateDataSource(githubUsersList)
                notifyDataSetChanged()
            }

            shouldRecyclerViewAnimationBeExecuted =
                if (!viewModel.provideStateValue<Boolean>(hasFirstSuccessfulCallBeenMade) || viewModel.provideStateValue(
                        hasUserTriggeredANewRequest
                    )
                ) {
                    (mergeAdapter.adapters[0] as HeaderAdapter).updateViewType(header)
                    true
                } else {
                    profileInfoRecyclerView.adapter?.notifyDataSetChanged()
                    applyViewVisibility(repositoryLoadingProgressBar, GONE)
                    false
                }

            if (viewModel.provideStateValue(hasUserTriggeredANewRequest)) viewModel.saveStateValue(
                hasUserTriggeredANewRequest,
                false
            )

            if (viewModel.provideStateValue(hasAnyUserRequestedUpdatedData)) {
                applyViewVisibility(githubProfileListSwipeRefreshLayout)
                (mergeAdapter.adapters[1] as GithubProfileAdapter).updateDataSource(githubUsersList)
                shouldRecyclerViewAnimationBeExecuted = true
                viewModel.saveStateValue(hasAnyUserRequestedUpdatedData, false)
            }

            if (shouldRecyclerViewAnimationBeExecuted)
                runLayoutAnimation(profileInfoRecyclerView)
            else
                shouldRecyclerViewAnimationBeExecuted = true

            applyViewVisibility(githubProfileListSwipeRefreshLayout)
        }
    }

    private fun onError() {

        viewModel.errorSingleLiveDataEvent.observe(this) { error ->

            if (this::countingIdlingResource.isInitialized)
                countingIdlingResource.decrement()

            if (viewModel.provideStateValue(hasUserTriggeredANewRequest)) viewModel.saveStateValue(
                hasUserTriggeredANewRequest,
                false
            )

            setupUpperViewsInteraction(true)
            applyViewVisibility(githubProfileListSwipeRefreshLayout)
            changeDrawable(actionIconImageView, R.drawable.ic_search)
            showErrorMessages(error)

            viewModel.saveStateValue(shouldASearchBePerformed, true)

            if (!shouldRecyclerViewAnimationBeExecuted)
                shouldRecyclerViewAnimationBeExecuted = true
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
        callApiThroughViewModel { viewModel.requestMoreGithubProfiles() }
    }

    @UnstableDefault
    private fun textInputEditTextNotEmptyRequiredCall() {
        hideKeyboard(searchProfileTextInputEditText)
        if (!isTextInputEditTextEmpty()) {
            applyViewVisibility(repositoryLoadingProgressBar, VISIBLE)
            viewModel.saveStateValue(hasUserTriggeredANewRequest, true)
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
            viewModel.saveStateValue(hasAnyUserRequestedUpdatedData, false)
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
                viewModel.saveStateValue(shouldASearchBePerformed, false)
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
        if (viewModel.provideStateValue(hasFirstSuccessfulCallBeenMade)) applyViewVisibility(
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
                        if (viewModel.provideStateValue(hasFirstSuccessfulCallBeenMade)) {
                            if (!viewModel.provideStateValue<Boolean>(isThereAnOngoingCall) && viewModel.provideStateValue(
                                    isRetryItemVisible
                                )
                            ) {
                                paginationCall()
                            }
                        } else
                            if (!isTextInputEditTextEmpty() && !viewModel.provideStateValue<Boolean>(
                                    isThereAnOngoingCall
                                )
                            ) {
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
        if (!viewModel.provideStateValue<Boolean>(isThereAnOngoingCall)) {
            if (viewModel.provideStateValue(shouldASearchBePerformed)) {
                textInputEditTextNotEmptyRequiredCall()
            } else {
                searchProfileTextInputEditText.setText("")
                changeDrawable(actionIconImageView, R.drawable.ic_search)
                if (!viewModel.provideStateValue<Boolean>(shouldASearchBePerformed)) viewModel.saveStateValue(
                    shouldASearchBePerformed,
                    true
                )
            }
        }
    }

    @UnstableDefault
    private fun setupRecyclerViewAddOnScrollListener() {

        profileInfoRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                val total = recyclerView.layoutManager?.itemCount
                val recyclerViewLayoutManager = provideRecyclerViewLayoutManager()

                if (recyclerViewLayoutManager.findFirstVisibleItemPosition() >= 2) {
                    if (backToTopButton.visibility != VISIBLE && backToTopButton.isClickable)
                        applyViewVisibility(backToTopButton, VISIBLE)
                } else {
                    if (backToTopButton.visibility != INVISIBLE)
                        applyViewVisibility(backToTopButton, INVISIBLE)
                    backToTopButton.isClickable = true
                }

                if (recyclerViewLayoutManager.findLastVisibleItemPosition() == total?.minus(1) && viewModel.provideStateValue(
                        hasFirstSuccessfulCallBeenMade
                    )
                ) {
                    shouldRecyclerViewAnimationBeExecuted = false
                    if (!viewModel.provideStateValue<Boolean>(isThereAnOngoingCall) && !viewModel.provideStateValue<Boolean>(
                            isRetryItemVisible
                        )
                    ) {
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

    private fun provideRecyclerViewLayoutManager() =
        profileInfoRecyclerView.layoutManager as LinearLayoutManager

    override fun onDestroy() {
        super.onDestroy()
        viewModel.saveStateValue(isThereAnyProfileToBeSearched, isTextInputEditTextEmpty())
    }
}