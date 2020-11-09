package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View.VISIBLE
import android.view.View.GONE
import android.view.View.INVISIBLE
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
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.adapter.GithubProfileAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.adapter.HeaderAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.adapter.TransientViewsAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.showUserRepositoryInformation.view.GithubProfileInfoActivity
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.ActivityFlow
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.OnItemClicked
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.empty
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.emptyString
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.endOfResults
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.githubProfileAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.githubProfileUrl
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.header
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.headerAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.loading
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.retry
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.transientViewsAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.viewHolder.OnItemSelectedListener
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel.GithubProfileViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.twenty
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.customViews.snackBar.CustomSnackBar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.applyBackgroundColor
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.applySwipeRefreshVisibilityAttributes
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.applyViewVisibility
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.changeDrawable
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.hideKeyboard
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.runTaskOnBackground
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.showErrorSnackBar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.showInternetConnectionStatusSnackBar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.network.NetworkChecking.checkIfInternetConnectionIsAvailable
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.network.NetworkChecking.internetConnectionAvailabilityObservable
import kotlinx.android.synthetic.main.activity_main.actionIconImageView
import kotlinx.android.synthetic.main.activity_main.backToTopButton
import kotlinx.android.synthetic.main.activity_main.githubProfileListSwipeRefreshLayout
import kotlinx.android.synthetic.main.activity_main.parentConstraintLayout
import kotlinx.android.synthetic.main.activity_main.profileInfoRecyclerView
import kotlinx.android.synthetic.main.activity_main.repositoryLoadingProgressBar
import kotlinx.android.synthetic.main.activity_main.searchProfileTextInputEditText
import org.koin.androidx.viewmodel.ext.android.viewModel

class GithubProfileListingActivity :
    AppCompatActivity(R.layout.activity_main), ActivityFlow {

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
            GithubProfileAdapter(obtainGithubProfileListener()),
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
        with(viewModel.successLiveData.value) {
            runTaskOnBackground {
                this?.let {
                    viewModel.obtainValueFromDataStore().textInputEditTextProfile.apply {
                        if (isNotEmpty()) {
                            searchProfileTextInputEditText.setText(this)
                            viewModel.saveValueToDataStore(
                                viewModel.obtainValueFromDataStore()
                                    .copy(shouldASearchBePerformed = true)
                            )
                        }
                    }

                    changeViewState(headerAdapter, header)

                    if (viewModel.obtainValueFromDataStore().isThereAnOngoingCall) {
                        applyViewVisibility(repositoryLoadingProgressBar, VISIBLE)
                        setupUpperViewsInteraction(false)
                        viewModel.saveValueToDataStore(
                            viewModel.obtainValueFromDataStore()
                                .copy(shouldASearchBePerformed = false)
                        )
                        changeDrawable(actionIconImageView, R.drawable.ic_close)
                    }

                    if (viewModel.obtainValueFromDataStore().hasASuccessfulCallAlreadyBeenMade &&
                        !viewModel.obtainValueFromDataStore().isTextInputEditTextEmpty
                    ) {
                        if (!viewModel.obtainValueFromDataStore().hasUserDeletedProfileText) {
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
                    viewModel.saveValueToDataStore()

                    viewModel.updateUIWithCache()
                    changeViewState(headerAdapter, header)
                    if (viewModel.obtainValueFromDataStore().isRetryViewVisible) {
                        changeViewState(transientViewsAdapter, retry)
                    }
                }
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
            runTaskOnBackground {
                if (!viewModel.obtainValueFromDataStore().hasASuccessfulCallAlreadyBeenMade
                ) {
                    applySwipeRefreshVisibilityAttributes(isSwipeEnabled = false)
                }
            }

            setOnRefreshListener {
                runTaskOnBackground {
                    viewModel.saveValueToDataStore(
                        viewModel.obtainValueFromDataStore()
                            .copy(hasUserRequestedUpdatedData = true)
                    )
                    updatedProfileCall()
                }
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
                    runTaskOnBackground {
                        if (!viewModel.obtainValueFromDataStore().shouldASearchBePerformed) {
                            viewModel.saveValueToDataStore(
                                viewModel.obtainValueFromDataStore()
                                    .copy(shouldASearchBePerformed = true)
                            )
                        }

                        changeDrawable(
                            actionIconImageView,
                            R.drawable.ic_search
                        )

                        text?.let {
                            if (it.isEmpty()) {
                                viewModel.saveValueToDataStore(
                                    viewModel.obtainValueFromDataStore()
                                        .copy(hasUserDeletedProfileText = true)
                                )
                            }
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

    private fun obtainGithubProfileListener(): OnItemSelectedListener {
        return object : OnItemSelectedListener {
            override fun onItemSelected(text: String) {
                runTaskOnBackground {
                    checkIfInternetConnectionIsAvailable(
                        applicationContext,
                        onConnectionAvailable = {
                            startActivity(
                                Intent(
                                    applicationContext,
                                    GithubProfileInfoActivity::class.java
                                ).putExtra(githubProfileUrl, text)
                            )
                        },
                        onConnectionUnavailable = {
                            showErrorSnackBar(
                                errorSnackBar,
                                R.string.no_connection_error
                            )
                        }
                    )
                }
            }
        }
    }

    override fun handleViewModel() {
        onSuccess()
        onError()
    }

    private fun onSuccess() {
        viewModel.successLiveData.observe(
            this,
            { githubUsersList ->
                runTaskOnBackground {
                    viewModel.saveValueToDataStore(
                        viewModel.obtainValueFromDataStore().copy(hasLastCallBeenUnsuccessful = false)
                    )
                    viewModel.saveValueToDataStore(
                        viewModel.obtainValueFromDataStore().copy(isThereAnOngoingCall = false)
                    )

                    if (this::countingIdlingResource.isInitialized) {
                        countingIdlingResource.decrement()
                    }

                    setupUpperViewsInteraction(true)

                    if (viewModel.obtainValueFromDataStore().hasUserDeletedProfileText &&
                        viewModel.obtainValueFromDataStore().textInputEditTextProfile.isNotEmpty()
                    ) {
                        viewModel.saveValueToDataStore(
                            viewModel.obtainValueFromDataStore().copy(shouldASearchBePerformed = true)
                        )
                    } else {
                        viewModel.saveValueToDataStore(
                            viewModel.obtainValueFromDataStore().copy(shouldASearchBePerformed = false)
                        )
                    }

                    if (!viewModel.obtainValueFromDataStore().isHeaderVisible) {
                        changeViewState(headerAdapter, header)
                    }

                    splitOnSuccess(githubUsersList)
                }
            }
        )
    }

    private suspend fun splitOnSuccess(githubUsersList: List<GithubProfileInformation>) {
        if (viewModel.obtainValueFromDataStore().numberOfItems < twenty) {
            changeViewState(transientViewsAdapter, endOfResults)
        } else {
            changeViewState(transientViewsAdapter, empty)
        }

        provideAdapter<GithubProfileAdapter>(githubProfileAdapter).apply {
            updateDataSource(githubUsersList)
            notifyDataSetChanged()
        }

        viewModel.saveValueToDataStore(
            viewModel.obtainValueFromDataStore()
                .copy(shouldRecyclerViewAnimationBeExecuted = shouldRecyclerViewAnimationBeExecuted())
        )

        if (viewModel.obtainValueFromDataStore().hasUserRequestedUpdatedData
        ) viewModel.saveValueToDataStore(
            viewModel.obtainValueFromDataStore().copy(hasUserRequestedUpdatedData = false)
        )

        if (viewModel.obtainValueFromDataStore().hasUserRequestedUpdatedData) {
            provideAdapter<GithubProfileAdapter>(githubProfileAdapter).updateDataSource(
                githubUsersList
            )

            viewModel.apply {
                saveValueToDataStore(
                    viewModel.obtainValueFromDataStore()
                        .copy(shouldRecyclerViewAnimationBeExecuted = true)
                )
                saveValueToDataStore(
                    viewModel.obtainValueFromDataStore()
                        .copy(hasUserRequestedUpdatedData = false)
                )
            }
        }

        if (viewModel.obtainValueFromDataStore().shouldRecyclerViewAnimationBeExecuted
        ) {
            runLayoutAnimation(profileInfoRecyclerView)
        } else {
            viewModel.saveValueToDataStore(
                viewModel.obtainValueFromDataStore()
                    .copy(shouldRecyclerViewAnimationBeExecuted = true)
            )
        }
    }

    private fun onError() {
        viewModel.errorSingleLiveDataEvent.observe(
            this,
            { error ->
                runTaskOnBackground {
                    viewModel.saveValueToDataStore(
                        viewModel.obtainValueFromDataStore().copy(hasLastCallBeenUnsuccessful = true)
                    )
                    viewModel.saveValueToDataStore(
                        viewModel.obtainValueFromDataStore().copy(isThereAnOngoingCall = false)
                    )

                    if (this::countingIdlingResource.isInitialized) {
                        countingIdlingResource.decrement()
                    }

                    if (viewModel.obtainValueFromDataStore().hasUserRequestedUpdatedData) {
                        viewModel.saveValueToDataStore(
                            viewModel.obtainValueFromDataStore()
                                .copy(hasUserRequestedUpdatedData = false)
                        )
                    }

                    setupUpperViewsInteraction(true)
                    githubProfileListSwipeRefreshLayout.applySwipeRefreshVisibilityAttributes(
                        isSwipeEnabled = true
                    )
                    changeDrawable(actionIconImageView, R.drawable.ic_search)
                    showErrorMessages(error)

                    if (!viewModel.obtainValueFromDataStore().shouldRecyclerViewAnimationBeExecuted) {
                        viewModel.saveValueToDataStore(
                            viewModel.obtainValueFromDataStore().copy(shouldASearchBePerformed = true)
                        )
                    }

                    if (viewModel.obtainValueFromDataStore().isPaginationLoadingViewVisible) {
                        changeViewState(transientViewsAdapter, retry)
                    }
                }
            }
        )
    }

    override fun setupExtras() {
        runTaskOnBackground {
            callToCheckIfInternetConnectionIsAvailable(
                onConnectionAvailable = {},
                onConnectionUnavailable = {
                    showInternetConnectionStatusSnackBar(
                        internetConnectivitySnackBar,
                        false
                    )
                }
            )
        }
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
        runTaskOnBackground {
            hideKeyboard(searchProfileTextInputEditText)
            if (!isTextInputEditTextEmpty()) {
                applyViewVisibility(repositoryLoadingProgressBar, VISIBLE)
                viewModel.saveValueToDataStore(
                    viewModel.obtainValueFromDataStore().copy(hasUserRequestedUpdatedData = true)
                )
                viewModel.saveValueToDataStore(
                    viewModel.obtainValueFromDataStore().copy(hasUserDeletedProfileText = false)
                )
                updatedProfileCall(searchProfileTextInputEditText.text.toString())
            } else {
                showErrorSnackBar(
                    errorSnackBar,
                    R.string.empty_field_error
                )
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
            scrollToTop(false)
            scheduleLayoutAnimation()
            githubProfileListSwipeRefreshLayout.applySwipeRefreshVisibilityAttributes()

            if (repositoryLoadingProgressBar.visibility == VISIBLE) {
                applyViewVisibility(repositoryLoadingProgressBar, GONE)
            }
        }
    }

    private inline fun callApiThroughViewModel(crossinline genericFunction: () -> Unit) {
        runTaskOnBackground {
            callToCheckIfInternetConnectionIsAvailable(
                onConnectionAvailable = {
                    if (!viewModel.obtainValueFromDataStore().isThereAnOngoingCall) {
                        viewModel.saveValueToDataStore(
                            viewModel.obtainValueFromDataStore().copy(isThereAnOngoingCall = true)
                        )
                    }

                    if (!viewModel.obtainValueFromDataStore().hasUserDeletedProfileText) {
                        setupUpperViewsInteraction(false)
                        changeDrawable(actionIconImageView, R.drawable.ic_close)
                    }

                    viewModel.saveValueToDataStore(
                        viewModel.obtainValueFromDataStore().copy(shouldASearchBePerformed = false)
                    )

                    genericFunction.invoke()

                    if (this::countingIdlingResource.isInitialized) {
                        countingIdlingResource.increment()
                    }
                },
                onConnectionUnavailable = {
                    viewModel.saveValueToDataStore(
                        viewModel.obtainValueFromDataStore()
                            .copy(hasLastCallBeenUnsuccessful = true)
                    )
                    githubProfileListSwipeRefreshLayout.applySwipeRefreshVisibilityAttributes()
                    if (viewModel.obtainValueFromDataStore().isPaginationLoadingViewVisible
                    ) {
                        changeViewState(transientViewsAdapter, retry)
                    }
                    showErrorMessages(R.string.no_connection_error)
                }
            )
        }
    }

    private fun showErrorMessages(message: Int) {
        hideKeyboard(searchProfileTextInputEditText)
        applyViewVisibility(repositoryLoadingProgressBar, GONE)
        setupUpperViewsInteraction(true)
        showErrorSnackBar(
            errorSnackBar,
            message,
            onDismissed = {
                runTaskOnBackground {
                    shouldRecallInternetConnectivitySnackBar()
                }
            }
        )
    }

    private suspend fun shouldRecallInternetConnectivitySnackBar(): Any {
        if (!viewModel.obtainValueFromDataStore().isRetryViewVisible) {
            return callToCheckIfInternetConnectionIsAvailable(
                onConnectionUnavailable = {
                    showInternetConnectionStatusSnackBar(
                        internetConnectivitySnackBar,
                        false
                    )
                }
            )
        }
        return emptyString
    }

    private suspend fun callToCheckIfInternetConnectionIsAvailable(
        onConnectionAvailable: suspend () -> Unit = {},
        onConnectionUnavailable: suspend () -> Unit = {}
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

                            runTaskOnBackground {
                                if (!viewModel.obtainValueFromDataStore().isThereAnOngoingCall &&
                                    viewModel.obtainValueFromDataStore().hasLastCallBeenUnsuccessful
                                ) {
                                    if (viewModel.obtainValueFromDataStore().isRetryViewVisible) {
                                        paginationCall()
                                    } else {
                                        textInputEditTextNotEmptyRequiredCall()
                                    }
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
        runTaskOnBackground {
            if (!viewModel.obtainValueFromDataStore().isThereAnOngoingCall) {
                if (viewModel.obtainValueFromDataStore().shouldASearchBePerformed) {
                    viewModel.saveValueToDataStore(
                        viewModel.obtainValueFromDataStore()
                            .copy(hasUserRequestedUpdatedData = true)
                    )
                    textInputEditTextNotEmptyRequiredCall()
                } else {
                    searchProfileTextInputEditText.setText(emptyString)
                    changeDrawable(actionIconImageView, R.drawable.ic_search)
                    if (!viewModel.obtainValueFromDataStore().shouldASearchBePerformed
                    ) viewModel.saveValueToDataStore(
                        viewModel.obtainValueFromDataStore().copy(
                            shouldASearchBePerformed = true
                        )
                    )
                    viewModel.saveValueToDataStore(
                        viewModel.obtainValueFromDataStore().copy(hasUserDeletedProfileText = true)
                    )
                }
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

                    runTaskOnBackground {
                        if (recyclerViewLayoutManager.findLastVisibleItemPosition() == total?.minus(
                                2
                            ) &&
                            viewModel.obtainValueFromDataStore().hasASuccessfulCallAlreadyBeenMade
                        ) {
                            viewModel.saveValueToDataStore(
                                viewModel.obtainValueFromDataStore()
                                    .copy(shouldRecyclerViewAnimationBeExecuted = false)
                            )
                            if (!viewModel.obtainValueFromDataStore().isThereAnOngoingCall &&
                                !viewModel.obtainValueFromDataStore().isRetryViewVisible &&
                                !viewModel.obtainValueFromDataStore().isEndOfResultsViewVisible
                            ) {
                                paginationCall()
                            }
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
        runTaskOnBackground {
            if (adapterPosition == headerAdapter) {
                (concatAdapter.adapters[adapterPosition] as HeaderAdapter).apply {
                    updateViewState(viewState)
                    viewModel.saveValueToDataStore(
                        viewModel.obtainValueFromDataStore().copy(isHeaderVisible = true)
                    )
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
//                            viewModel.obtainValueFromDataStore(
//                                numberOfItems, Constants.zero
//                            ).plus(2)
//                        )
                        }
                        else -> saveItemView()
                    }
                }
            }
        }
    }

    private suspend fun saveItemView(
        isEndOfResultsItemVisible: Boolean = false,
        isPaginationLoadingItemVisible: Boolean = false,
        isRetryItemVisible: Boolean = false
    ) {
        viewModel.apply {
            saveValueToDataStore(obtainValueFromDataStore().copy(isEndOfResultsViewVisible = isEndOfResultsItemVisible))
            saveValueToDataStore(
                obtainValueFromDataStore().copy(
                    isPaginationLoadingViewVisible = isPaginationLoadingItemVisible
                )
            )
            saveValueToDataStore(obtainValueFromDataStore().copy(isRetryViewVisible = isRetryItemVisible))
        }
    }

    private suspend fun shouldRecyclerViewAnimationBeExecuted(): Boolean {
        return if (!viewModel.obtainValueFromDataStore().hasASuccessfulCallAlreadyBeenMade ||
            viewModel.obtainValueFromDataStore().hasUserRequestedUpdatedData
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
        runTaskOnBackground {
            viewModel.apply {
                saveValueToDataStore(
                    obtainValueFromDataStore().copy(
                        isTextInputEditTextEmpty = isTextInputEditTextEmpty()
                    )
                )
                if (!isTextInputEditTextEmpty()) {
                    saveValueToDataStore(
                        obtainValueFromDataStore().copy(
                            textInputEditTextProfile = searchProfileTextInputEditText.text.toString()
                        )
                    )
                }
            }
        }
    }
}
