package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.activity

import android.content.res.Configuration
import android.os.Bundle
import android.view.View.*
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.idling.CountingIdlingResource
import com.google.android.material.snackbar.Snackbar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.adapter.GithubProfileAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.adapter.HeaderAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.adapter.TransientViewsAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel.GithubProfileViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.ActivityFlow
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.OnItemClicked
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.empty
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.emptyString
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.endOfResults
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.githubProfileAdapter
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
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.shouldASearchBePerformed
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.shouldRecyclerViewAnimationBeExecuted
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.textInputEditTextProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.transientViewsAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.twenty
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.zero
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.customViews.snackBar.CustomSnackBar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.*
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.network.NetworkChecking.checkIfInternetConnectionIsAvailable
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.network.NetworkChecking.internetConnectionAvailabilityObservable
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class GithubProfileListingActivity :
    AppCompatActivity(R.layout.activity_main),
    ActivityFlow {

    private lateinit var countingIdlingResource: CountingIdlingResource

    private val errorSnackBar by lazy {
        Snackbar.make(
            findViewById(android.R.id.content),
            R.string.generic_exception_and_generic_error,
            Snackbar.LENGTH_SHORT
        )
    }

    private val internetConnectivitySnackBar by lazy {
        CustomSnackBar.make(findViewById(android.R.id.content))
    }

    private val viewModel by viewModel<GithubProfileViewModel>()

    private val concatAdapter by lazy {
        ConcatAdapter(
            HeaderAdapter(R.string.github_user_list_header),
            GithubProfileAdapter(errorSnackBar),
            TransientViewsAdapter()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setupView()
        handleViewModel()
        setupExtras()
    }

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
        viewModel.successLiveData.value?.let {
            viewModel.retrieveFromSharedPreferences(
                textInputEditTextProfile,
                emptyString
            ).apply {
                if (isNotEmpty()) {
                    searchProfileTextInputEditText.setText(this)
                    viewModel.saveToSharedPreferences(
                        shouldASearchBePerformed,
                        true
                    )
                }
            }

            changeViewState(headerAdapter, header)

            if (viewModel.retrieveFromSharedPreferences(isThereAnOngoingCall, false)) {
                applyViewVisibility(repositoryLoadingProgressBar, VISIBLE)
                setupUpperViewsInteraction(false)
                viewModel.saveToSharedPreferences(shouldASearchBePerformed, false)
                changeDrawable(actionIconImageView, R.drawable.ic_close)
            }

            if (viewModel.retrieveFromSharedPreferences(
                    hasASuccessfulCallAlreadyBeenMade,
                    false
                ) && !viewModel.retrieveFromSharedPreferences(
                        Constants.isTextInputEditTextEmpty,
                        true
                    )
            ) {
                if (!viewModel.retrieveFromSharedPreferences(hasUserDeletedProfileText, false)) {
                    changeDrawable(
                        actionIconImageView,
                        R.drawable.ic_close
                    )
                }
            }

            if (provideRecyclerViewLayoutManager().findFirstVisibleItemPosition() >= 2) {
                applyViewVisibility(backToTopButton, VISIBLE)
            }
        } ?: run {
            viewModel.clearSharedPreferences()

            viewModel.updateUIWithCache()
            changeViewState(headerAdapter, header)
            if (viewModel.retrieveFromSharedPreferences(isRetryViewVisible, false)) {
                changeViewState(transientViewsAdapter, retry)
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

    private fun setupSwipeRefreshLayout() {
        githubProfileListSwipeRefreshLayout.apply {
            if (!viewModel.retrieveFromSharedPreferences(
                    hasASuccessfulCallAlreadyBeenMade,
                    false
                )
            ) {
                applySwipeRefreshVisibilityAttributes(isSwipeEnabled = false)
            }

            setOnRefreshListener {
                viewModel.saveToSharedPreferences(hasUserRequestedUpdatedData, true)
                updatedProfileCall()
            }
        }
    }

    private fun setupRecyclerView() {
        profileInfoRecyclerView.apply {
            setHasFixedSize(true)
            adapter = concatAdapter
            setupRecyclerViewAddOnScrollListener()
        }
    }

    private fun setupTextInputEditText() {
        with(searchProfileTextInputEditText) {
            setOnEditorActionListener(
                TextView.OnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        textInputEditTextNotEmptyRequiredCall()
                        return@OnEditorActionListener true
                    }
                    false
                }
            )

            addTextChangedListener {
                doOnTextChanged { text, _, _, _ ->

                    if (!viewModel.retrieveFromSharedPreferences(
                            shouldASearchBePerformed,
                            true
                        )
                    ) viewModel.saveToSharedPreferences(
                        shouldASearchBePerformed,
                        true
                    )

                    changeDrawable(
                        actionIconImageView,
                        R.drawable.ic_search
                    )

                    text?.let {
                        if (it.isEmpty()) {
                            viewModel.saveToSharedPreferences(hasUserDeletedProfileText, true)
                        }
                    }
                }
            }
        }
    }

    private fun initializeAdapterCallback() {
        provideAdapter<TransientViewsAdapter>(transientViewsAdapter).setOnItemClicked(
            object :
                OnItemClicked {
                override fun onItemClick(adapterPosition: Int, id: Int) {
                    paginationCall()
                }
            }
        )
    }

    override fun handleViewModel() {
        onSuccess()
        onError()
    }

    private fun onSuccess() {
        viewModel.successLiveData.observe(
            this,
            { githubUsersList ->

                viewModel.saveToSharedPreferences(hasLastCallBeenUnsuccessful, false)
                viewModel.saveToSharedPreferences(isThereAnOngoingCall, false)

                if (this::countingIdlingResource.isInitialized) {
                    countingIdlingResource.decrement()
                }

                setupUpperViewsInteraction(true)

                if (viewModel.retrieveFromSharedPreferences(
                        hasUserDeletedProfileText,
                        false
                    ) && viewModel.retrieveFromSharedPreferences(
                            textInputEditTextProfile,
                            emptyString
                        ).isNotEmpty()
                ) {
                    viewModel.saveToSharedPreferences(shouldASearchBePerformed, true)
                } else {
                    viewModel.saveToSharedPreferences(shouldASearchBePerformed, false)
                }

                if (!viewModel.retrieveFromSharedPreferences(isHeaderVisible, false)) {
                    changeViewState(headerAdapter, header)
                }

                if (viewModel.retrieveFromSharedPreferences(numberOfItems, zero) < twenty) {
                    changeViewState(transientViewsAdapter, endOfResults)
                } else {
                    changeViewState(transientViewsAdapter, empty)
                }

                provideAdapter<GithubProfileAdapter>(githubProfileAdapter).apply {
                    updateDataSource(githubUsersList)
                    notifyDataSetChanged()
                }

                viewModel.saveToSharedPreferences(
                    shouldRecyclerViewAnimationBeExecuted,
                    shouldRecyclerViewAnimationBeExecuted()
                )

                if (viewModel.retrieveFromSharedPreferences(
                        hasUserRequestedUpdatedData,
                        false
                    )
                ) viewModel.saveToSharedPreferences(
                    hasUserRequestedUpdatedData,
                    false
                )

                if (viewModel.retrieveFromSharedPreferences(hasUserRequestedUpdatedData, false)) {
                    provideAdapter<GithubProfileAdapter>(githubProfileAdapter).updateDataSource(
                        githubUsersList
                    )

                    viewModel.apply {
                        saveToSharedPreferences(shouldRecyclerViewAnimationBeExecuted, true)
                        saveToSharedPreferences(hasUserRequestedUpdatedData, false)
                    }
                }

                if (viewModel.retrieveFromSharedPreferences(
                        shouldRecyclerViewAnimationBeExecuted,
                        true
                    )
                ) {
                    runLayoutAnimation(profileInfoRecyclerView)
                } else {
                    viewModel.saveToSharedPreferences(shouldRecyclerViewAnimationBeExecuted, true)
                }
            }
        )
    }

    private fun onError() {
        viewModel.errorSingleLiveDataEvent.observe(
            this,
            { error ->

                viewModel.saveToSharedPreferences(hasLastCallBeenUnsuccessful, true)
                viewModel.saveToSharedPreferences(isThereAnOngoingCall, false)

                if (this::countingIdlingResource.isInitialized) {
                    countingIdlingResource.decrement()
                }

                if (viewModel.retrieveFromSharedPreferences(
                        hasUserRequestedUpdatedData,
                        false
                    )
                ) viewModel.saveToSharedPreferences(
                    hasUserRequestedUpdatedData,
                    false
                )

                setupUpperViewsInteraction(true)
                githubProfileListSwipeRefreshLayout.applySwipeRefreshVisibilityAttributes(isSwipeEnabled = true)
                changeDrawable(actionIconImageView, R.drawable.ic_search)
                showErrorMessages(error)

                if (!viewModel.retrieveFromSharedPreferences(
                        shouldRecyclerViewAnimationBeExecuted,
                        true
                    )
                ) {
                    viewModel.saveToSharedPreferences(shouldASearchBePerformed, true)
                }

                if (viewModel.retrieveFromSharedPreferences(isPaginationLoadingViewVisible, false)) {
                    changeViewState(transientViewsAdapter, retry)
                }
            }
        )
    }

    override fun setupExtras() {
        checkIfInternetConnectionIsAvailableCaller(
            onConnectionAvailable = {},
            onConnectionUnavailable = {
                showInternetConnectionStatusSnackBar(
                    internetConnectivitySnackBar,
                    false
                )
            }
        )
        setupInternetConnectionObserver()
    }

    private fun updatedProfileCall(profile: String = "") {
        if (profile.isNotEmpty()) {
            callApiThroughViewModel { viewModel.requestUpdatedGithubProfiles(profile) }
        } else {
            callApiThroughViewModel { viewModel.requestUpdatedGithubProfiles() }
        }
    }

    private fun paginationCall() {
        changeViewState(transientViewsAdapter, loading)
        callApiThroughViewModel { viewModel.requestMoreGithubProfiles() }
    }

    private fun textInputEditTextNotEmptyRequiredCall() {
        hideKeyboard(searchProfileTextInputEditText)
        if (!isTextInputEditTextEmpty()) {
            applyViewVisibility(repositoryLoadingProgressBar, VISIBLE)
            viewModel.saveToSharedPreferences(hasUserRequestedUpdatedData, true)
            viewModel.saveToSharedPreferences(hasUserDeletedProfileText, false)
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
            githubProfileListSwipeRefreshLayout.applySwipeRefreshVisibilityAttributes()

            if (repositoryLoadingProgressBar.visibility == VISIBLE) {
                applyViewVisibility(repositoryLoadingProgressBar, GONE)
            }
        }
    }

    private inline fun callApiThroughViewModel(crossinline genericFunction: () -> Unit) {
        checkIfInternetConnectionIsAvailableCaller(
            onConnectionAvailable = {
                if (!viewModel.retrieveFromSharedPreferences(isThereAnOngoingCall, false)) {
                    viewModel.saveToSharedPreferences(isThereAnOngoingCall, true)
                }

                if (!viewModel.retrieveFromSharedPreferences(hasUserDeletedProfileText, false)) {
                    setupUpperViewsInteraction(false)
                    changeDrawable(actionIconImageView, R.drawable.ic_close)
                }

                viewModel.saveToSharedPreferences(shouldASearchBePerformed, false)

                genericFunction.invoke()

                if (this::countingIdlingResource.isInitialized) {
                    countingIdlingResource.increment()
                }
            },
            onConnectionUnavailable = {
                viewModel.saveToSharedPreferences(hasLastCallBeenUnsuccessful, true)
                githubProfileListSwipeRefreshLayout.applySwipeRefreshVisibilityAttributes()
                if (viewModel.retrieveFromSharedPreferences(
                        isPaginationLoadingViewVisible,
                        false
                    )
                ) {
                    changeViewState(transientViewsAdapter, retry)
                }
                showErrorMessages(R.string.no_connection_error)
            }
        )
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
            }
        )
    }

    private fun shouldRecallInternetConnectivitySnackBar(): Any {
        if (!viewModel.retrieveFromSharedPreferences(
                isRetryViewVisible,
                false
            )
        ) return checkIfInternetConnectionIsAvailableCaller(
            onConnectionUnavailable = {
                showInternetConnectionStatusSnackBar(
                    internetConnectivitySnackBar,
                    false
                )
            }
        )
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

    private fun setupInternetConnectionObserver() {
        internetConnectionAvailabilityObservable(applicationContext)
            .observe(
                this,
                { isInternetAvailable ->
                    when (isInternetAvailable) {
                        true -> {
                            showInternetConnectionStatusSnackBar(
                                internetConnectivitySnackBar,
                                true
                            )

                            if (!viewModel.retrieveFromSharedPreferences(
                                    isThereAnOngoingCall,
                                    false
                                ) && viewModel.retrieveFromSharedPreferences(
                                        hasLastCallBeenUnsuccessful,
                                        false
                                    )
                            ) {
                                if (viewModel.retrieveFromSharedPreferences(
                                        isRetryViewVisible,
                                        false
                                    )
                                ) {
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
            )
    }

    private fun handleActionIconClick() {
        if (!viewModel.retrieveFromSharedPreferences(isThereAnOngoingCall, false)) {
            if (viewModel.retrieveFromSharedPreferences(shouldASearchBePerformed, true)) {
                viewModel.saveToSharedPreferences(hasUserRequestedUpdatedData, true)
                textInputEditTextNotEmptyRequiredCall()
            } else {
                searchProfileTextInputEditText.setText(emptyString)
                changeDrawable(actionIconImageView, R.drawable.ic_search)
                if (!viewModel.retrieveFromSharedPreferences(
                        shouldASearchBePerformed,
                        true
                    )
                ) viewModel.saveToSharedPreferences(
                    shouldASearchBePerformed,
                    true
                )
                viewModel.saveToSharedPreferences(hasUserDeletedProfileText, true)
            }
        }
    }

    private fun setupRecyclerViewAddOnScrollListener() {
        profileInfoRecyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val total = recyclerView.layoutManager?.itemCount
                    val recyclerViewLayoutManager = provideRecyclerViewLayoutManager()

                    if (recyclerViewLayoutManager.findFirstVisibleItemPosition() >= 2) {
                        if (backToTopButton.visibility != VISIBLE && backToTopButton.isClickable) {
                            applyViewVisibility(backToTopButton, VISIBLE)
                        }
                    } else {
                        if (backToTopButton.visibility != INVISIBLE) {
                            applyViewVisibility(backToTopButton, INVISIBLE)
                        }
                        backToTopButton.isClickable = true
                    }

                    if (recyclerViewLayoutManager.findLastVisibleItemPosition() == total?.minus(2) &&
                        viewModel.retrieveFromSharedPreferences(
                                hasASuccessfulCallAlreadyBeenMade,
                                false
                            )
                    ) {
                        viewModel.saveToSharedPreferences(shouldRecyclerViewAnimationBeExecuted, false)
                        if (!viewModel.retrieveFromSharedPreferences(isThereAnOngoingCall, false) &&
                            !viewModel.retrieveFromSharedPreferences(isRetryViewVisible, false) &&
                            !viewModel.retrieveFromSharedPreferences(isEndOfResultsViewVisible, false)
                        ) {
                            paginationCall()
                        }
                    }
                }
            }
        )
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
        if (shouldScrollBeSmooth) {
            profileInfoRecyclerView.smoothScrollToPosition(0)
        } else {
            profileInfoRecyclerView.scrollToPosition(0)
        }
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
            (concatAdapter.adapters[adapterPosition] as HeaderAdapter).apply {
                updateViewState(viewState)
                viewModel.saveToSharedPreferences(isHeaderVisible, true)
            }
        } else {
            (concatAdapter.adapters[adapterPosition] as TransientViewsAdapter).apply {
                updateViewState(viewState)
                notifyDataSetChanged()

                when (viewState) {
                    endOfResults -> saveItemView(isEndOfResultsItemVisible = true)
                    loading -> saveItemView(isPaginationLoadingItemVisible = true)
                    retry -> {
                        saveItemView(isRetryItemVisible = true)
//                        profileInfoRecyclerView.smoothScrollToPosition(
//                            viewModel.retrieveFromSharedPreferences(
//                                numberOfItems, Constants.zero
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
            saveToSharedPreferences(isEndOfResultsViewVisible, isEndOfResultsItemVisible)
            saveToSharedPreferences(isPaginationLoadingViewVisible, isPaginationLoadingItemVisible)
            saveToSharedPreferences(isRetryViewVisible, isRetryItemVisible)
        }
    }

    private fun shouldRecyclerViewAnimationBeExecuted(): Boolean {
        return if (!viewModel.retrieveFromSharedPreferences(
                hasASuccessfulCallAlreadyBeenMade,
                false
            ) || viewModel.retrieveFromSharedPreferences(
                    hasUserRequestedUpdatedData,
                    false
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
            0 -> concatAdapter.adapters[adapterPosition] as T
            1 -> concatAdapter.adapters[adapterPosition] as T
            else -> concatAdapter.adapters[adapterPosition] as T
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.apply {
            saveToSharedPreferences(Constants.isTextInputEditTextEmpty, isTextInputEditTextEmpty())
            if (!isTextInputEditTextEmpty()) {
                viewModel.saveToSharedPreferences(
                    textInputEditTextProfile,
                    searchProfileTextInputEditText.text.toString()
                )
            }
        }
    }
}
