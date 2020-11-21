package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
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
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.databinding.ActivityGithubProfileListingBinding
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.adapter.GithubProfileAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.adapter.HeaderAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.adapter.TransientViewsAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.viewHolder.OnItemSelectedListener
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel.GithubProfileViewModel
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
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.twenty
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.customViews.snackBar.CustomSnackBar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.applyBackgroundColor
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.applySwipeRefreshVisibilityAttributes
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.applyViewVisibility
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.castValue
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.changeDrawable
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.hideKeyboard
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.runTaskOnBackground
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.showErrorSnackBar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.showInternetConnectionStatusSnackBar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.network.NetworkChecking.checkIfInternetConnectionIsAvailable
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.network.NetworkChecking.internetConnectionAvailabilityObservable
import org.koin.androidx.viewmodel.ext.android.viewModel

class GithubProfileListingActivity : AppCompatActivity(), ActivityFlow {

    private lateinit var binding: ActivityGithubProfileListingBinding

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
        binding = ActivityGithubProfileListingBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
                            binding.searchProfileTextInputEditText.setText(this)
                            viewModel.saveValueToDataStore(
                                viewModel.obtainValueFromDataStore()
                                    .copy(shouldASearchBePerformed = true)
                            )
                        }
                    }

                    changeViewState(headerAdapter, header)

                    if (viewModel.obtainValueFromDataStore().isThereAnOngoingCall) {
                        binding.repositoryLoadingProgressBar.applyViewVisibility(VISIBLE)
                        setupUpperViewsInteraction(false)
                        viewModel.saveValueToDataStore(
                            viewModel.obtainValueFromDataStore()
                                .copy(shouldASearchBePerformed = false)
                        )
                        binding.actionIconImageView.changeDrawable(R.drawable.ic_close)
                    }

                    if (viewModel.obtainValueFromDataStore().hasASuccessfulCallAlreadyBeenMade &&
                        !viewModel.obtainValueFromDataStore().isTextInputEditTextEmpty
                    ) {
                        if (!viewModel.obtainValueFromDataStore().hasUserDeletedProfileText) {
                            binding.actionIconImageView.changeDrawable(R.drawable.ic_close)
                        }
                    }

                    if (provideRecyclerViewLayoutManager().findFirstVisibleItemPosition() >= 2) {
                        binding.backToTopButton.applyViewVisibility(VISIBLE)
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
                binding.parentConstraintLayout.applyBackgroundColor(android.R.color.black)
        }
    }

    private fun setupActionViews() {
        binding.actionIconImageView.setOnClickListener {
            handleActionIconClick()
        }

        binding.backToTopButton.setOnClickListener {
            it.isClickable = false
            it.applyViewVisibility(INVISIBLE)
            scrollToTop(true)
        }
    }

    private fun setupSwipeRefreshLayout() {
        binding.githubProfileListSwipeRefreshLayout.apply {
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
        binding.profileInfoRecyclerView.apply {
            setHasFixedSize(true)
            adapter = concatAdapter
            setupRecyclerViewAddOnScrollListener()
        }
    }

    private fun setupTextInputEditText() {
        with(binding.searchProfileTextInputEditText) {
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

                        binding.actionIconImageView.changeDrawable(R.drawable.ic_search)

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
                            errorSnackBar.showErrorSnackBar(
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
                        viewModel.obtainValueFromDataStore()
                            .copy(hasLastCallBeenUnsuccessful = false)
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
                            viewModel.obtainValueFromDataStore()
                                .copy(shouldASearchBePerformed = true)
                        )
                    } else {
                        viewModel.saveValueToDataStore(
                            viewModel.obtainValueFromDataStore()
                                .copy(shouldASearchBePerformed = false)
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
            runLayoutAnimation(binding.profileInfoRecyclerView)
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
                        viewModel.obtainValueFromDataStore()
                            .copy(hasLastCallBeenUnsuccessful = true)
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
                    binding.githubProfileListSwipeRefreshLayout.applySwipeRefreshVisibilityAttributes(
                        isSwipeEnabled = true
                    )
                    binding.actionIconImageView.changeDrawable(R.drawable.ic_search)
                    showErrorMessages(error)

                    if (!viewModel.obtainValueFromDataStore().shouldRecyclerViewAnimationBeExecuted) {
                        viewModel.saveValueToDataStore(
                            viewModel.obtainValueFromDataStore()
                                .copy(shouldASearchBePerformed = true)
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
                onConnectionUnavailable = {
                    internetConnectivitySnackBar.showInternetConnectionStatusSnackBar(
                        false
                    )
                }
            )
            setupInternetConnectionObserver()
        }
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
            binding.searchProfileTextInputEditText.hideKeyboard()
            if (!isTextInputEditTextEmpty()) {
                binding.repositoryLoadingProgressBar.applyViewVisibility(VISIBLE)
                viewModel.saveValueToDataStore(
                    viewModel.obtainValueFromDataStore().copy(hasUserRequestedUpdatedData = true)
                )
                viewModel.saveValueToDataStore(
                    viewModel.obtainValueFromDataStore().copy(hasUserDeletedProfileText = false)
                )
                updatedProfileCall(binding.searchProfileTextInputEditText.text.toString())
            } else {
                errorSnackBar.showErrorSnackBar(
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
            binding.githubProfileListSwipeRefreshLayout.applySwipeRefreshVisibilityAttributes()

            if (binding.repositoryLoadingProgressBar.visibility == VISIBLE) {
                binding.repositoryLoadingProgressBar.applyViewVisibility(GONE)
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
                        binding.actionIconImageView.changeDrawable(R.drawable.ic_close)
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
                    binding.githubProfileListSwipeRefreshLayout.applySwipeRefreshVisibilityAttributes()
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
        binding.searchProfileTextInputEditText.hideKeyboard()
        binding.repositoryLoadingProgressBar.applyViewVisibility(GONE)
        setupUpperViewsInteraction(true)
        errorSnackBar.showErrorSnackBar(
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
                    internetConnectivitySnackBar.showInternetConnectionStatusSnackBar(false)
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
                            internetConnectivitySnackBar.showInternetConnectionStatusSnackBar(
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
                        false -> internetConnectivitySnackBar.showInternetConnectionStatusSnackBar(
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
                    binding.searchProfileTextInputEditText.setText(emptyString)
                    binding.actionIconImageView.changeDrawable(R.drawable.ic_search)
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
        binding.profileInfoRecyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val total = recyclerView.layoutManager?.itemCount
                    val recyclerViewLayoutManager = provideRecyclerViewLayoutManager()

                    binding.backToTopButton.apply {
                        if (recyclerViewLayoutManager.findFirstVisibleItemPosition() >= 2) {
                            if (visibility != VISIBLE && isClickable) {
                                binding.backToTopButton.applyViewVisibility(VISIBLE)
                            }
                        } else {
                            if (visibility != INVISIBLE) {
                                this.applyViewVisibility(INVISIBLE)
                            }
                            isClickable = true
                        }
                    }

                    runTaskOnBackground {
                        if (recyclerViewLayoutManager.findLastVisibleItemPosition() == total?.minus(2) &&
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
        binding.actionIconImageView.apply {
            isClickable = shouldUsersBeAbleToInteractWithTheUpperViews
            isFocusable = shouldUsersBeAbleToInteractWithTheUpperViews
        }

        with(binding.searchProfileTextInputEditText) {
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
            binding.profileInfoRecyclerView.smoothScrollToPosition(0)
        } else {
            binding.profileInfoRecyclerView.scrollToPosition(0)
        }
    }

    private fun isTextInputEditTextEmpty() =
        binding.searchProfileTextInputEditText.text.toString().isEmpty()

    fun bindIdlingResource(receivedCountingIdlingResource: CountingIdlingResource) {
        countingIdlingResource = receivedCountingIdlingResource
    }

    private fun provideRecyclerViewLayoutManager() =
        castValue<LinearLayoutManager>(binding.profileInfoRecyclerView.layoutManager)

    private fun changeViewState(adapterPosition: Int, viewState: Int) {
        runTaskOnBackground {
            if (adapterPosition == headerAdapter) {
                (castValue<HeaderAdapter>(concatAdapter.adapters[adapterPosition])).apply {
                    updateViewState(viewState)
                    viewModel.saveValueToDataStore(
                        viewModel.obtainValueFromDataStore().copy(isHeaderVisible = true)
                    )
                }
            } else {
                (castValue<TransientViewsAdapter>(concatAdapter.adapters[adapterPosition])).apply {
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
            binding.profileInfoRecyclerView.adapter?.notifyDataSetChanged()
            binding.repositoryLoadingProgressBar.applyViewVisibility(GONE)
            false
        }
    }

    private inline fun <reified T> provideAdapter(adapterPosition: Int) =
        castValue<T>(concatAdapter.adapters[adapterPosition])

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
                            textInputEditTextProfile = binding.searchProfileTextInputEditText.text.toString()
                        )
                    )
                }
            }
        }
    }
}
