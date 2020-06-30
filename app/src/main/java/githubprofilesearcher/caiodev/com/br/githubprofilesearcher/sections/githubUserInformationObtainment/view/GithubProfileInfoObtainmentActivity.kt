package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.View.*
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.MergeAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.idling.CountingIdlingResource
import com.google.android.material.snackbar.Snackbar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.adapter.GithubProfileAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.adapter.HeaderAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.adapter.TransientViewsAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.viewTypes.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel.GithubProfileViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.ActivityFlow
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.empty
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.emptyString
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.endOfResults
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.githubProfileAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.githubProfilesList
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.hasASuccessfulCallAlreadyBeenMade
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.hasLastCallBeenUnsuccessful
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.hasUserDeletedProfileText
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.hasUserRequestedUpdatedData
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.header
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.headerAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.isEndOfResultsViewVisible
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.isHeaderVisible
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.isPaginationLoadingViewVisible
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.isRetryViewVisible
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.isThereAnOngoingCall
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.loading
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.numberOfItems
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.retry
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.savedStateHandleArguments
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.shouldASearchBePerformed
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.shouldRecyclerViewAnimationBeExecuted
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.textInputEditTextProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.transientViewsAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.customViews.snackBar.CustomSnackBar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.*
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.interfaces.OnItemClicked
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.network.NetworkChecking.checkIfInternetConnectionIsAvailable
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.network.NetworkChecking.internetConnectionAvailabilityObservable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.serialization.UnstableDefault
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class GithubProfileInfoObtainmentActivity :
    AppCompatActivity(R.layout.activity_main),
    ActivityFlow {

    private lateinit var countingIdlingResource: CountingIdlingResource

    private val errorSnackBar by lazy {
        Snackbar.make(
            findViewById<View>(android.R.id.content),
            R.string.generic_exception_and_generic_error,
            Snackbar.LENGTH_SHORT
        )
    }

    private val internetConnectivitySnackBar by lazy {
        CustomSnackBar.make(findViewById(android.R.id.content))
    }

    private val viewModel by stateViewModel<GithubProfileViewModel>(bundle = { savedStateHandleArguments })

    private val mergeAdapter by lazy {
        MergeAdapter(
            HeaderAdapter(R.string.github_user_list_header),
            GithubProfileAdapter(errorSnackBar),
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
        setupActionViews()
        setupSwipeRefreshLayout()
        setupRecyclerView()
        setupTextInputEditText()
        initializeAdapterCallback()
    }

    private fun bindViewModelDataToUIInCaseOfOrientationChanges() {

        viewModel.provideStateValue<String>(
            textInputEditTextProfile
        ).apply {
            if (isNotEmpty()) {
                searchProfileTextInputEditText.setText(this)
                viewModel.saveStateValue(
                    shouldASearchBePerformed,
                    true
                )
            }
        }

        viewModel.successLiveData.value?.let {

            changeViewState(headerAdapter, header)
            applySwipeRefreshVisibilityAttributes(githubProfileListSwipeRefreshLayout)

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

                if (!viewModel.provideStateValue<Boolean>(hasUserDeletedProfileText))
                    changeDrawable(
                        actionIconImageView,
                        R.drawable.ic_close
                    )
            }

            if (provideRecyclerViewLayoutManager().findFirstVisibleItemPosition() >= 2) {
                applyViewVisibility(backToTopButton, VISIBLE)
            }

        } ?: run {
            if (viewModel.provideStateValue<List<GithubProfileInformation>>(githubProfilesList)
                    .isNotEmpty()
            ) {
                viewModel.updateUIInCaseOfSystemInitiatedProcessDeath()
                changeViewState(headerAdapter, header)
                if (viewModel.provideStateValue(isRetryViewVisible)) {
                    changeViewState(transientViewsAdapter, retry)
                }
                applySwipeRefreshVisibilityAttributes(githubProfileListSwipeRefreshLayout)
            }
        }
    }

    private fun setupDarkMode() {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES ->
                applyBackgroundColor(
                    parentConstraintLayout,
                    android.R.color.black
                )
        }
    }

    @UnstableDefault
    private fun setupActionViews() {

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
                updatedProfileCall()
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
                doOnTextChanged { text, _, _, _ ->

                    if (!viewModel.provideStateValue<Boolean>(shouldASearchBePerformed)) viewModel.saveStateValue(
                        shouldASearchBePerformed,
                        true
                    )

                    changeDrawable(
                        actionIconImageView,
                        R.drawable.ic_search
                    )

                    text?.let {
                        if (it.isEmpty())
                            viewModel.saveStateValue(hasUserDeletedProfileText, true)
                    }
                }
            }
        }
    }

    @UnstableDefault
    private fun initializeAdapterCallback() {
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

            if (viewModel.provideStateValue(hasUserDeletedProfileText) && viewModel.provideStateValue<String>(
                    textInputEditTextProfile
                ).isNotEmpty()
            ) {
                viewModel.saveStateValue(shouldASearchBePerformed, true)
            } else {
                viewModel.saveStateValue(shouldASearchBePerformed, false)
            }

            if (!viewModel.provideStateValue<Boolean>(isHeaderVisible)) {
                changeViewState(headerAdapter, header)
            }

            if (viewModel.provideStateValue<Int>(numberOfItems) < 20)
                changeViewState(transientViewsAdapter, endOfResults)
            else
                changeViewState(transientViewsAdapter, empty)

            provideAdapter<GithubProfileAdapter>(githubProfileAdapter).apply {
                updateDataSource(githubUsersList)
                notifyDataSetChanged()
            }

            viewModel.saveStateValue(
                shouldRecyclerViewAnimationBeExecuted,
                shouldRecyclerViewAnimationBeExecuted()
            )

            if (viewModel.provideStateValue(hasUserRequestedUpdatedData)) viewModel.saveStateValue(
                hasUserRequestedUpdatedData,
                false
            )

            if (viewModel.provideStateValue(hasUserRequestedUpdatedData)) {

                provideAdapter<GithubProfileAdapter>(githubProfileAdapter).updateDataSource(
                    githubUsersList
                )

                viewModel.apply {
                    saveStateValue(shouldRecyclerViewAnimationBeExecuted, true)
                    saveStateValue(hasUserRequestedUpdatedData, false)
                }
            }

            if (viewModel.provideStateValue(shouldRecyclerViewAnimationBeExecuted))
                runLayoutAnimation(profileInfoRecyclerView)
            else
                viewModel.saveStateValue(shouldRecyclerViewAnimationBeExecuted, true)
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

            if (!viewModel.provideStateValue<Boolean>(shouldRecyclerViewAnimationBeExecuted))
                viewModel.saveStateValue(shouldASearchBePerformed, true)

            if (viewModel.provideStateValue(isPaginationLoadingViewVisible))
                changeViewState(transientViewsAdapter, retry)
        }
    }

    @UnstableDefault
    override fun setupExtras() {
        checkIfInternetConnectionIsAvailableCaller(
            onConnectionAvailable = {},
            onConnectionUnavailable = {
                showInternetConnectionStatusSnackBar(
                    internetConnectivitySnackBar,
                    false
                )
            })
        setupInternetConnectionObserver()
    }

    @UnstableDefault
    private fun updatedProfileCall(profile: String = "") {
        if (profile.isNotEmpty())
            callApiThroughViewModel { viewModel.requestUpdatedGithubProfiles(profile) }
        else
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
            viewModel.saveStateValue(hasUserDeletedProfileText, false)
            updatedProfileCall(searchProfileTextInputEditText.text.toString())
        } else {
            showErrorSnackBar(
                errorSnackBar,
                R.string.empty_field_error
            )
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
                if (!viewModel.provideStateValue<Boolean>(isThereAnOngoingCall))
                    viewModel.saveStateValue(isThereAnOngoingCall, true)

                if (!viewModel.provideStateValue<Boolean>(hasUserDeletedProfileText)) {
                    setupUpperViewsInteraction(false)
                    changeDrawable(actionIconImageView, R.drawable.ic_close)
                }

                viewModel.saveStateValue(shouldASearchBePerformed, false)

                genericFunction.invoke()

                if (this::countingIdlingResource.isInitialized)
                    countingIdlingResource.increment()
            },
            onConnectionUnavailable = {
                viewModel.saveStateValue(hasLastCallBeenUnsuccessful, true)
                applySwipeRefreshVisibilityAttributes(githubProfileListSwipeRefreshLayout)
                if (viewModel.provideStateValue(isPaginationLoadingViewVisible))
                    changeViewState(transientViewsAdapter, retry)
                showErrorMessages(R.string.no_connection_error)
            })
    }

    private fun showErrorMessages(message: Int) {
        hideKeyboard(searchProfileTextInputEditText)
        applyViewVisibility(repositoryLoadingProgressBar, GONE)
        setupUpperViewsInteraction(true)
        showErrorSnackBar(
            errorSnackBar,
            message,
            onDismissed = {
                shouldRecallInternetConnectivitySnackBar()
            })
    }

    private fun shouldRecallInternetConnectivitySnackBar(): Any {
        if (!viewModel.provideStateValue<Boolean>(
                isRetryViewVisible
            )
        ) return checkIfInternetConnectionIsAvailableCaller(
            onConnectionUnavailable = {
                showInternetConnectionStatusSnackBar(
                    internetConnectivitySnackBar,
                    false
                )
            })
        return emptyString
    }

    private fun checkIfInternetConnectionIsAvailableCaller(
        onConnectionAvailable: () -> Unit = {},
        onConnectionUnavailable: () -> Unit = {}
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
                        showInternetConnectionStatusSnackBar(
                            internetConnectivitySnackBar,
                            true
                        )

                        if (!viewModel.provideStateValue<Boolean>(isThereAnOngoingCall) && viewModel.provideStateValue(
                                hasLastCallBeenUnsuccessful
                            )
                        ) {
                            if (viewModel.provideStateValue(isRetryViewVisible)) {
                                paginationCall()
                            } else {
                                textInputEditTextNotEmptyRequiredCall()
                            }
                        }
                    }

                    false -> showInternetConnectionStatusSnackBar(
                        internetConnectivitySnackBar,
                        false
                    )
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
                searchProfileTextInputEditText.setText(emptyString)
                changeDrawable(actionIconImageView, R.drawable.ic_search)
                if (!viewModel.provideStateValue<Boolean>(shouldASearchBePerformed)) viewModel.saveStateValue(
                    shouldASearchBePerformed,
                    true
                )
                viewModel.saveStateValue(hasUserDeletedProfileText, true)
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
                    viewModel.saveStateValue(shouldRecyclerViewAnimationBeExecuted, false)
                    if (!viewModel.provideStateValue<Boolean>(isThereAnOngoingCall) &&
                        !viewModel.provideStateValue<Boolean>(isRetryViewVisible) &&
                        !viewModel.provideStateValue<Boolean>(isEndOfResultsViewVisible)
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
                viewModel.saveStateValue(isHeaderVisible, true)
            }

        } else {

            (mergeAdapter.adapters[adapterPosition] as TransientViewsAdapter).apply {

                updateViewState(viewState)
                notifyDataSetChanged()

                when (viewState) {
                    endOfResults -> saveItemView(isEndOfResultsItemVisible = true)
                    loading -> saveItemView(isPaginationLoadingItemVisible = true)
                    retry -> {
                        saveItemView(isRetryItemVisible = true)
//                        profileInfoRecyclerView.smoothScrollToPosition(
//                            viewModel.provideStateValue<Int>(
//                                numberOfItems
//                            ).plus(2)
//                        )
                    }
                    else -> saveItemView()
                }
            }
        }
    }

    private fun saveItemView(
        isEndOfResultsItemVisible: Boolean = false,
        isPaginationLoadingItemVisible: Boolean = false,
        isRetryItemVisible: Boolean = false
    ) {
        viewModel.apply {
            saveStateValue(isEndOfResultsViewVisible, isEndOfResultsItemVisible)
            saveStateValue(isPaginationLoadingViewVisible, isPaginationLoadingItemVisible)
            saveStateValue(isRetryViewVisible, isRetryItemVisible)
        }
    }

    private fun shouldRecyclerViewAnimationBeExecuted(): Boolean {
        return if (!viewModel.provideStateValue<Boolean>(hasASuccessfulCallAlreadyBeenMade) || viewModel.provideStateValue(
                hasUserRequestedUpdatedData
            )
        ) {
            true
        } else {
            profileInfoRecyclerView.adapter?.notifyDataSetChanged()
            applyViewVisibility(repositoryLoadingProgressBar, GONE)
            false
        }
    }

    private inline fun <reified T> provideAdapter(adapterPosition: Int): T {
        return when (adapterPosition) {
            0 -> (mergeAdapter.adapters[adapterPosition] as T)
            1 -> (mergeAdapter.adapters[adapterPosition] as T)
            else -> (mergeAdapter.adapters[adapterPosition] as T)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.apply {
            saveStateValue(Constants.isTextInputEditTextEmpty, isTextInputEditTextEmpty())
            if (!isTextInputEditTextEmpty()) {
                viewModel.saveStateValue(
                    textInputEditTextProfile,
                    searchProfileTextInputEditText.text.toString()
                )
            }
        }
    }
}