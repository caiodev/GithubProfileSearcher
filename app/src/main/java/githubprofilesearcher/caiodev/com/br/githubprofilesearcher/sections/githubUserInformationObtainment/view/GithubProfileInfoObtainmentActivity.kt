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
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.empty
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.endOfResults
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.githubProfileAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.hasASuccessfulCallAlreadyBeenMade
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.hasLastCallBeenUnsuccessful
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.hasUserRequestedUpdatedData
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.header
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.headerAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.isEndOfResultsItemVisible
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.isPaginationLoadingItemVisible
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.isRetryItemVisible
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.isThereAnOngoingCall
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.loading
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.numberOfItems
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.retry
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.shouldASearchBePerformed
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.transientViewsAdapter
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

        if (viewModel.provideStateValue(hasASuccessfulCallAlreadyBeenMade) && !viewModel.provideStateValue<Boolean>(
                Constants.isTextInputEditTextEmpty
            )
        ) {

            changeDrawable(
                actionIconImageView,
                R.drawable.ic_close
            )

            applySwipeRefreshVisibilityAttributes(githubProfileListSwipeRefreshLayout)
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

            if (!viewModel.provideStateValue<Boolean>(hasASuccessfulCallAlreadyBeenMade))
                applySwipeRefreshVisibilityAttributes(this, isEnabled = false)

            setOnRefreshListener {
                viewModel.saveStateValue(hasUserRequestedUpdatedData, true)
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

        provideAdapter<GithubProfileAdapter>(githubProfileAdapter).setOnItemClicked(object :
            OnItemClicked {

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

        provideAdapter<TransientViewsAdapter>(transientViewsAdapter).setOnItemClicked(object :
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

            viewModel.saveStateValue(hasLastCallBeenUnsuccessful, false)
            viewModel.saveStateValue(isThereAnOngoingCall, false)

            if (this::countingIdlingResource.isInitialized)
                countingIdlingResource.decrement()

            setupUpperViewsInteraction(true)

            if (!viewModel.provideStateValue<Boolean>(hasASuccessfulCallAlreadyBeenMade))
                viewModel.saveStateValue(hasASuccessfulCallAlreadyBeenMade, true)

            viewModel.saveStateValue(shouldASearchBePerformed, false)

            changeViewState(headerAdapter, header)

            if (viewModel.provideStateValue<Int>(numberOfItems) < 20)
                changeViewState(transientViewsAdapter, endOfResults)
            else
                changeViewState(transientViewsAdapter, empty)

            provideAdapter<GithubProfileAdapter>(githubProfileAdapter).apply {
                updateDataSource(githubUsersList)
                notifyDataSetChanged()
            }

            shouldRecyclerViewAnimationBeExecuted =
                if (!viewModel.provideStateValue<Boolean>(hasASuccessfulCallAlreadyBeenMade) || viewModel.provideStateValue(
                        hasUserRequestedUpdatedData
                    )
                ) {
                    true
                } else {

                    profileInfoRecyclerView.adapter?.notifyDataSetChanged()

                    applyViewVisibility(repositoryLoadingProgressBar, GONE)
                    false
                }

            if (viewModel.provideStateValue(hasUserRequestedUpdatedData)) viewModel.saveStateValue(
                hasUserRequestedUpdatedData,
                false
            )

            if (viewModel.provideStateValue(hasUserRequestedUpdatedData)) {
                provideAdapter<GithubProfileAdapter>(githubProfileAdapter).updateDataSource(
                    githubUsersList
                )
                shouldRecyclerViewAnimationBeExecuted = true
                viewModel.saveStateValue(hasUserRequestedUpdatedData, false)
            }

            if (shouldRecyclerViewAnimationBeExecuted)
                runLayoutAnimation(profileInfoRecyclerView)
            else
                shouldRecyclerViewAnimationBeExecuted = true
        }
    }

    private fun onError() {

        viewModel.errorSingleLiveDataEvent.observe(this) { error ->

            viewModel.saveStateValue(hasLastCallBeenUnsuccessful, true)
            viewModel.saveStateValue(isThereAnOngoingCall, false)

            if (this::countingIdlingResource.isInitialized)
                countingIdlingResource.decrement()

            if (viewModel.provideStateValue(hasUserRequestedUpdatedData)) viewModel.saveStateValue(
                hasUserRequestedUpdatedData,
                false
            )

            setupUpperViewsInteraction(true)
            applySwipeRefreshVisibilityAttributes(githubProfileListSwipeRefreshLayout)
            changeDrawable(actionIconImageView, R.drawable.ic_search)
            showErrorMessages(error)

            viewModel.saveStateValue(shouldASearchBePerformed, true)

            if (!shouldRecyclerViewAnimationBeExecuted)
                shouldRecyclerViewAnimationBeExecuted = true

            if (viewModel.provideStateValue(hasASuccessfulCallAlreadyBeenMade))
                changeViewState(transientViewsAdapter, retry)
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
        changeViewState(transientViewsAdapter, loading)
        callApiThroughViewModel { viewModel.requestMoreGithubProfiles() }
    }

    @UnstableDefault
    private fun textInputEditTextNotEmptyRequiredCall() {
        hideKeyboard(searchProfileTextInputEditText)
        if (!isTextInputEditTextEmpty()) {
            applyViewVisibility(repositoryLoadingProgressBar, VISIBLE)
            viewModel.saveStateValue(hasUserRequestedUpdatedData, true)
            callApiThroughViewModel {
                viewModel.requestUpdatedGithubProfiles(
                    searchProfileTextInputEditText.text.toString()
                )
            }
        } else {
            showSnackBar(
                this,
                getString(R.string.empty_field_error)
            ) {}
        }
    }

    private fun runLayoutAnimation(recyclerView: RecyclerView) {
        with(recyclerView) {
            layoutAnimation = AnimationUtils.loadLayoutAnimation(
                context,
                R.anim.layout_animation_fall_down
            )

            adapter?.notifyDataSetChanged()

            scrollToTop(false)
            scheduleLayoutAnimation()
            applySwipeRefreshVisibilityAttributes(githubProfileListSwipeRefreshLayout)

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

                if (!viewModel.provideStateValue<Boolean>(isThereAnOngoingCall))
                    viewModel.saveStateValue(isThereAnOngoingCall, true)

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
                        if (viewModel.provideStateValue(hasASuccessfulCallAlreadyBeenMade)) {
                            if (!viewModel.provideStateValue<Boolean>(isThereAnOngoingCall) && viewModel.provideStateValue(
                                    isRetryItemVisible
                                )
                            ) {
                                paginationCall()
                            }
                        } else
                            if (!viewModel.provideStateValue<Boolean>(isThereAnOngoingCall) &&
                                !viewModel.provideStateValue<Boolean>(hasUserRequestedUpdatedData) &&
                                viewModel.provideStateValue(hasLastCallBeenUnsuccessful)
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
                viewModel.saveStateValue(hasUserRequestedUpdatedData, true)
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

                if (recyclerViewLayoutManager.findLastVisibleItemPosition() == total?.minus(2) &&
                    viewModel.provideStateValue(hasASuccessfulCallAlreadyBeenMade)
                ) {
                    shouldRecyclerViewAnimationBeExecuted = false
                    if (!viewModel.provideStateValue<Boolean>(isThereAnOngoingCall) &&
                        !viewModel.provideStateValue<Boolean>(isRetryItemVisible) &&
                        !viewModel.provideStateValue<Boolean>(isEndOfResultsItemVisible)
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

    private fun changeViewState(adapterPosition: Int, viewState: Int) {
        if (adapterPosition == headerAdapter) {
            (mergeAdapter.adapters[adapterPosition] as HeaderAdapter).apply {
                updateViewState(viewState)
                notifyDataSetChanged()
            }
        } else {
            (mergeAdapter.adapters[adapterPosition] as TransientViewsAdapter).apply {
                updateViewState(viewState)

                when (viewState) {

                    endOfResults -> {
                        viewModel.apply {
                            saveStateValue(isEndOfResultsItemVisible, true)
                            saveStateValue(isPaginationLoadingItemVisible, false)
                            saveStateValue(isRetryItemVisible, false)
                        }
                    }

                    loading -> {
                        viewModel.apply {
                            saveStateValue(isEndOfResultsItemVisible, false)
                            saveStateValue(isPaginationLoadingItemVisible, true)
                            saveStateValue(isRetryItemVisible, false)
                        }
                    }

                    retry -> {
                        viewModel.apply {
                            saveStateValue(isEndOfResultsItemVisible, false)
                            saveStateValue(isPaginationLoadingItemVisible, false)
                            saveStateValue(isRetryItemVisible, true)
                        }
                    }

                    else -> {
                        viewModel.apply {
                            saveStateValue(isEndOfResultsItemVisible, false)
                            saveStateValue(isPaginationLoadingItemVisible, false)
                            saveStateValue(isRetryItemVisible, false)
                        }
                    }
                }

                notifyDataSetChanged()
            }
        }
    }

    private inline fun <reified T> provideAdapter(adapterPosition: Int): T {
        return when (adapterPosition) {
            0 -> (mergeAdapter.adapters[adapterPosition] as T)
            1 -> (mergeAdapter.adapters[adapterPosition] as T)
            else -> (mergeAdapter.adapters[adapterPosition] as T)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.saveStateValue(Constants.isTextInputEditTextEmpty, isTextInputEditTextEmpty())
    }
}