package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.view.activity

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.idling.CountingIdlingResource
import com.google.android.material.snackbar.Snackbar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.databinding.ActivityProfileListingBinding
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.view.adapter.HeaderAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.view.adapter.HeaderAdapter.Companion.header
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.view.adapter.ProfileAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.view.adapter.TransientViewsAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.view.viewHolder.OnItemSelectedListener
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.view.viewHolder.transientItemViews.EmptyViewHolder.Companion.empty
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.view.viewHolder.transientItemViews.EndOfResultsViewHolder.Companion.endOfResults
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.view.viewHolder.transientItemViews.LoadingViewHolder.Companion.loading
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.view.viewHolder.transientItemViews.RetryViewHolder.Companion.retry
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.viewModel.ProfileViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.LifecycleOwnerFlow
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.OnItemClicked
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.Available
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.ClientSide
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.Connect
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.InitialError
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.InitialSuccess
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.LocalPopulation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.SSLHandshake
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.SearchLimitReached
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.SearchQuotaReached
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.ServerSide
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.SocketTimeout
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.SuccessWithBody
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.Unavailable
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.UnknownHost
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.string.emptyString
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.cast.ValueCasting.castTo
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.customViews.snackBar.CustomSnackBar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.applyBackgroundColor
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.applySwipeRefreshVisibilityAttributes
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.applyViewVisibility
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.changeDrawable
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.hideKeyboard
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.runTaskOnBackground
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.showErrorSnackBar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.showInternetConnectionStatusSnackBar
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileListingActivity : ComponentActivity(), LifecycleOwnerFlow {

    private lateinit var binding: ActivityProfileListingBinding
    private lateinit var countingIdlingResource: CountingIdlingResource

    private val errorSnackBar by lazy {
        Snackbar.make(
            findViewById(android.R.id.content),
            R.string.generic,
            Snackbar.LENGTH_SHORT
        )
    }

    private val internetConnectivitySnackBar by lazy {
        CustomSnackBar.make(findViewById(android.R.id.content))
    }

    private val viewModel by viewModel<ProfileViewModel>()

    private val concatAdapter by lazy {
        ConcatAdapter(
            HeaderAdapter(R.string.user_list_header),
            ProfileAdapter(obtainProfileListener()),
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.apply {
            saveValueToDataStore(
                obtainValueFromDataStore().toBuilder()
                    .setIsTextInputEditTextNotEmpty(isTextInputEditTextNotEmpty()).build()
            )
            if (isTextInputEditTextNotEmpty()) {
                saveValueToDataStore(
                    obtainValueFromDataStore().toBuilder()
                        .setProfile(binding.searchProfileTextInputEditText.text.toString()).build()
                )
            }
        }
    }

    override fun setupView() {
        binding = ActivityProfileListingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupDarkMode()
        setupSwipeRefreshLayout()
        bindViewModelDataToUIInCaseOfOrientationChanges()
        setupActionViews()
        setupRecyclerView()
        setupTextInputEditText()
        initializeAdapterCallback()
    }

    private fun bindViewModelDataToUIInCaseOfOrientationChanges() {
        runTaskOnBackground {
            viewModel.intermediateSharedFlow.collect {
                when (it) {
                    LocalPopulation -> {
                        viewModel.updateUIWithCache()
                        if (viewModel.obtainValueFromDataStore().isRetryViewVisible) {
                            changeViewState(transientViewsAdapter, retry)
                        }
                        restoreScreenState()
                    }
                    else -> Unit
                }
            }
        }
        restoreScreenState()
        viewModel.checkDataAtStartup()
    }

    private fun restoreScreenState() {
        if (!viewModel.obtainValueFromDataStore().hasUserDeletedProfileText &&
            viewModel.obtainValueFromDataStore().profile.isNotEmpty()
        ) {
            binding.searchProfileTextInputEditText.setText(
                viewModel.obtainValueFromDataStore().profile
            )
            viewModel.saveValueToDataStore(
                viewModel.obtainValueFromDataStore().toBuilder()
                    .setShouldASearchBePerformed(true).build()
            )
        }

        if (viewModel.obtainValueFromDataStore().hasASuccessfulCallAlreadyBeenMade) {
            changeViewState(headerAdapter, header)
        }
        if (viewModel.obtainValueFromDataStore().isThereAnOngoingCall) {
            binding.repositoryLoadingProgressBar.applyViewVisibility(VISIBLE)
            setupUpperViewsInteraction(false)
            viewModel.saveValueToDataStore(
                viewModel.obtainValueFromDataStore().toBuilder()
                    .setShouldASearchBePerformed(false).build()
            )
            binding.actionIconImageView.changeDrawable(R.drawable.ic_close)
        }

        viewModel.obtainValueFromDataStore().apply {
            if (isLocalPopulation && isTextInputEditTextNotEmpty && !hasUserDeletedProfileText
            ) {
                binding.searchProfileTextInputEditText.setSelection(viewModel.obtainValueFromDataStore().profile.length)
                viewModel.saveValueToDataStore(
                    viewModel.obtainValueFromDataStore().toBuilder()
                        .setShouldASearchBePerformed(false).build()
                )
                binding.actionIconImageView.changeDrawable(R.drawable.ic_close)
            } else {
                viewModel.saveValueToDataStore(
                    viewModel.obtainValueFromDataStore().toBuilder()
                        .setShouldASearchBePerformed(true).build()
                )
                binding.actionIconImageView.changeDrawable(R.drawable.ic_search)
            }
        }

        if (binding.searchProfileTextInputEditText.text.toString().isEmpty()) {
            viewModel.saveValueToDataStore(
                viewModel.obtainValueFromDataStore().toBuilder()
                    .setShouldASearchBePerformed(true).build()
            )
        } else {
            if (viewModel.obtainValueFromDataStore().hasASuccessfulCallAlreadyBeenMade) {
                viewModel.saveValueToDataStore(
                    viewModel.obtainValueFromDataStore().toBuilder()
                        .setShouldASearchBePerformed(false).build()
                )
                binding.actionIconImageView.changeDrawable(R.drawable.ic_close)
            }
        }

        provideRecyclerViewLayoutManager()?.findFirstVisibleItemPosition()
            ?.let { position ->
                if (position >= 2) {
                    binding.backToTopButton.applyViewVisibility(VISIBLE)
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
            if (!viewModel.obtainValueFromDataStore().isLocalPopulation &&
                !viewModel.obtainValueFromDataStore().hasASuccessfulCallAlreadyBeenMade
            ) {
                applySwipeRefreshVisibilityAttributes(isSwipeEnabled = false)
            }

            setOnRefreshListener {
                viewModel.saveValueToDataStore(
                    viewModel.obtainValueFromDataStore()
                        .toBuilder().setHasUserRequestedUpdatedData(true).build()
                )
                updatedProfileCall()
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
                    if (!viewModel.obtainValueFromDataStore().shouldASearchBePerformed) {
                        viewModel.saveValueToDataStore(
                            viewModel.obtainValueFromDataStore().toBuilder()
                                .setShouldASearchBePerformed(true).build()
                        )
                    }

                    binding.actionIconImageView.changeDrawable(R.drawable.ic_search)

                    text?.let {
                        if (it.isEmpty()) {
                            viewModel.saveValueToDataStore(
                                viewModel.obtainValueFromDataStore().toBuilder()
                                    .setHasUserDeletedProfileText(true).build()
                            )
                        }
                    }
                }
            }
        }
    }

    private fun initializeAdapterCallback() {
        provideAdapter<TransientViewsAdapter>(transientViewsAdapter)?.setOnItemClicked(
            object :
                OnItemClicked {
                override fun onItemClick(adapterPosition: Int, id: Int) {
                    paginationCall()
                }
            }
        )
    }

    private fun obtainProfileListener(): OnItemSelectedListener {
        return object : OnItemSelectedListener {
            override fun onItemSelected(text: String) {
                handleConnectionState(
                    onConnectionAvailable = {
                        launchBrowser(text)
                    },
                    onConnectionUnavailable = {
                        errorSnackBar.showErrorSnackBar(
                            R.string.no_connection
                        )
                    }
                )
            }
        }
    }

    override fun handleViewModel() {
        onSuccess()
        onError()
    }

    private fun onSuccess() {
        runTaskOnBackground {
            viewModel.successStateFlow.collect { state ->
                when (state) {
                    is InitialSuccess -> Unit
                    is SuccessWithBody<*> -> {
                        when (val value = state.data) {
                            is List<*> -> {
                                setupUpperViewsInteraction(true)
                                if (this::countingIdlingResource.isInitialized) {
                                    countingIdlingResource.decrement()
                                }
                                if (!viewModel.obtainValueFromDataStore().isHeaderVisible) {
                                    changeViewState(headerAdapter, header)
                                }
                                viewModel.castTo<List<UserProfile>>(value)?.let {
                                    splitOnSuccess(it, state.totalPages)
                                }
                            }
                        }
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun splitOnSuccess(githubUsersList: List<UserProfile>, totalPages: Int) {

        if (viewModel.obtainValueFromDataStore().pageNumber == totalPages) {
            changeViewState(transientViewsAdapter, endOfResults)
        } else {
            changeViewState(transientViewsAdapter, empty)
        }

        provideAdapter<ProfileAdapter>(githubProfileAdapter)?.apply {
            updateDataSource(githubUsersList)
            notifyDataSetChanged()
        }

        viewModel.saveValueToDataStore(
            viewModel.obtainValueFromDataStore().toBuilder()
                .setShouldRecyclerViewAnimationBeExecuted(shouldRecyclerViewAnimationBeExecuted())
                .build()
        )

        if (viewModel.obtainValueFromDataStore().hasUserRequestedUpdatedData
        ) viewModel.saveValueToDataStore(
            viewModel.obtainValueFromDataStore().toBuilder().setHasUserRequestedUpdatedData(false)
                .build()
        )

        if (viewModel.obtainValueFromDataStore().hasUserRequestedUpdatedData) {
            provideAdapter<ProfileAdapter>(githubProfileAdapter)?.updateDataSource(
                githubUsersList
            )

            viewModel.apply {
                saveValueToDataStore(
                    viewModel.obtainValueFromDataStore().toBuilder()
                        .setShouldRecyclerViewAnimationBeExecuted(true).build()
                )
                saveValueToDataStore(
                    viewModel.obtainValueFromDataStore().toBuilder()
                        .setHasUserRequestedUpdatedData(false).build()
                )
            }
        }

        if (viewModel.obtainValueFromDataStore().shouldRecyclerViewAnimationBeExecuted
        ) {
            runLayoutAnimation(binding.profileInfoRecyclerView)
        } else {
            viewModel.saveValueToDataStore(
                viewModel.obtainValueFromDataStore().toBuilder()
                    .setShouldRecyclerViewAnimationBeExecuted(true).build()
            )
        }
    }

    private fun onError() {
        runTaskOnBackground {
            viewModel.errorSharedFlow.collect { error ->
                when (error) {
                    UnknownHost, SocketTimeout, Connect ->
                        showErrorMessage(R.string.unknown_host_and_socket_timeout)
                    SSLHandshake -> showErrorMessage(R.string.ssl_handshake)
                    ClientSide -> showErrorMessage(R.string.client_side)
                    ServerSide -> showErrorMessage(R.string.server_side)
                    SearchQuotaReached -> showErrorMessage(R.string.query_limit)
                    SearchLimitReached -> showErrorMessage(R.string.limit_of_profile_results)
                    InitialError -> Unit
                    else -> showErrorMessage(R.string.generic)
                }
            }
        }
    }

    override fun setupExtras() {
        handleConnectionState(
            onConnectionUnavailable = {
                internetConnectivitySnackBar.showInternetConnectionStatusSnackBar(
                    false
                )
            }
        )
        setupInternetConnectionObserver()
    }

    private fun updatedProfileCall(profile: String = "") {
        if (profile.isNotEmpty()) {
            callApiThroughViewModel { viewModel.requestUpdatedProfiles(profile) }
        } else {
            callApiThroughViewModel { viewModel.requestUpdatedProfiles() }
        }
    }

    private fun paginationCall() {
        changeViewState(transientViewsAdapter, loading)
        callApiThroughViewModel { viewModel.paginateProfiles() }
    }

    private fun textInputEditTextNotEmptyRequiredCall() {
        binding.searchProfileTextInputEditText.hideKeyboard()
        if (isTextInputEditTextNotEmpty()) {
            binding.repositoryLoadingProgressBar.applyViewVisibility(VISIBLE)
            viewModel.saveValueToDataStore(
                viewModel.obtainValueFromDataStore().toBuilder()
                    .setHasUserRequestedUpdatedData(true).build()
            )
            viewModel.saveValueToDataStore(
                viewModel.obtainValueFromDataStore().toBuilder().setHasUserDeletedProfileText(false)
                    .build()
            )
            updatedProfileCall(binding.searchProfileTextInputEditText.text.toString())
        } else {
            errorSnackBar.showErrorSnackBar(
                R.string.empty_field
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
            binding.githubProfileListSwipeRefreshLayout.applySwipeRefreshVisibilityAttributes()

            if (binding.repositoryLoadingProgressBar.visibility == VISIBLE) {
                binding.repositoryLoadingProgressBar.applyViewVisibility(GONE)
            }
        }
    }

    private inline fun callApiThroughViewModel(crossinline task: () -> Unit) {
        handleConnectionState(
            onConnectionAvailable = {
                viewModel.saveValueToDataStore(
                    viewModel.obtainValueFromDataStore().toBuilder().setIsThereAnOngoingCall(true)
                        .build()
                )

                if (!viewModel.obtainValueFromDataStore().hasUserDeletedProfileText) {
                    setupUpperViewsInteraction(false)
                    binding.actionIconImageView.changeDrawable(R.drawable.ic_close)
                }

                viewModel.saveValueToDataStore(
                    viewModel.obtainValueFromDataStore().toBuilder()
                        .setShouldASearchBePerformed(false).build()
                )

                task()

                if (this::countingIdlingResource.isInitialized) {
                    countingIdlingResource.increment()
                }
            },
            onConnectionUnavailable = {
                viewModel.saveValueToDataStore(
                    viewModel.obtainValueFromDataStore().toBuilder()
                        .setHasLastCallBeenUnsuccessful(true).build()
                )
                binding.githubProfileListSwipeRefreshLayout.applySwipeRefreshVisibilityAttributes()
                if (viewModel.obtainValueFromDataStore().isPaginationLoadingViewVisible
                ) {
                    changeViewState(transientViewsAdapter, retry)
                }
                showErrorMessage(R.string.no_connection)
            }
        )
    }

    private fun showErrorMessage(@StringRes message: Int) {
        viewModel.saveValueToDataStore(
            viewModel.obtainValueFromDataStore().toBuilder()
                .setHasLastCallBeenUnsuccessful(true).build()
        )
        viewModel.saveValueToDataStore(
            viewModel.obtainValueFromDataStore().toBuilder().setIsThereAnOngoingCall(false)
                .build()
        )

        if (this::countingIdlingResource.isInitialized) {
            countingIdlingResource.decrement()
        }

        if (viewModel.obtainValueFromDataStore().hasUserRequestedUpdatedData) {
            viewModel.saveValueToDataStore(
                viewModel.obtainValueFromDataStore().toBuilder()
                    .setHasUserRequestedUpdatedData(false).build()
            )
        }

        binding.githubProfileListSwipeRefreshLayout.applySwipeRefreshVisibilityAttributes()

        binding.actionIconImageView.changeDrawable(R.drawable.ic_search)
        binding.searchProfileTextInputEditText.hideKeyboard()
        binding.repositoryLoadingProgressBar.applyViewVisibility(GONE)
        setupUpperViewsInteraction(true)
        errorSnackBar.showErrorSnackBar(
            message,
            onDismissed = {
                recallConnectivitySnackBar()
            }
        )

        if (!viewModel.obtainValueFromDataStore().shouldRecyclerViewAnimationBeExecuted) {
            viewModel.saveValueToDataStore(
                viewModel.obtainValueFromDataStore().toBuilder()
                    .setShouldASearchBePerformed(true).build()
            )
        }

        if (viewModel.obtainValueFromDataStore().isPaginationLoadingViewVisible) {
            changeViewState(transientViewsAdapter, retry)
        }
    }

    private fun recallConnectivitySnackBar(): Any {
        if (!viewModel.obtainValueFromDataStore().isRetryViewVisible) {
            return handleConnectionState(
                onConnectionUnavailable = {
                    internetConnectivitySnackBar.showInternetConnectionStatusSnackBar(false)
                }
            )
        }
        return emptyString()
    }

    private fun setupInternetConnectionObserver() {
        runTaskOnBackground {
            viewModel.provideConnectionObserver().collect { connectionState ->
                when (connectionState) {
                    Available -> {
                        internetConnectivitySnackBar.showInternetConnectionStatusSnackBar(
                            true
                        )
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
                    Unavailable -> {
                        internetConnectivitySnackBar.showInternetConnectionStatusSnackBar(false)
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun handleActionIconClick() {
        if (!viewModel.obtainValueFromDataStore().isThereAnOngoingCall) {
            if (viewModel.obtainValueFromDataStore().shouldASearchBePerformed) {
                viewModel.saveValueToDataStore(
                    viewModel.obtainValueFromDataStore().toBuilder()
                        .setHasUserRequestedUpdatedData(true).build()
                )
                textInputEditTextNotEmptyRequiredCall()
            } else {
                binding.searchProfileTextInputEditText.setText(emptyString())
                binding.actionIconImageView.changeDrawable(R.drawable.ic_search)
                viewModel.saveValueToDataStore(
                    viewModel.obtainValueFromDataStore().toBuilder()
                        .setShouldASearchBePerformed(true).build()
                )
                viewModel.saveValueToDataStore(
                    viewModel.obtainValueFromDataStore().toBuilder()
                        .setHasUserDeletedProfileText(true).build()
                )
            }
        }
    }

    private fun setupRecyclerViewAddOnScrollListener() {
        binding.profileInfoRecyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    val total = recyclerView.layoutManager?.itemCount
                    val recyclerViewLayoutManager = provideRecyclerViewLayoutManager()

                    binding.backToTopButton.apply {
                        recyclerViewLayoutManager?.findFirstVisibleItemPosition()?.let {
                            if (it >= 2) {
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
                    }

                    if (recyclerViewLayoutManager?.findLastVisibleItemPosition() == total?.minus(2) &&
                        viewModel.obtainValueFromDataStore().hasASuccessfulCallAlreadyBeenMade
                    ) {
                        viewModel.saveValueToDataStore(
                            viewModel.obtainValueFromDataStore().toBuilder()
                                .setShouldRecyclerViewAnimationBeExecuted(false).build()
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
        )
    }

    private fun setupUpperViewsInteraction(isInteractive: Boolean) {
        binding.actionIconImageView.apply {
            isClickable = isInteractive
            isFocusable = isInteractive
        }

        with(binding.searchProfileTextInputEditText) {
            isClickable = isInteractive
            isCursorVisible = isInteractive
            isFocusable = isInteractive
            isFocusableInTouchMode = isInteractive
            isLongClickable = isInteractive
            if (isInteractive) requestFocus()
        }
    }

    private fun scrollToTop(shouldScrollBeSmooth: Boolean) {
        if (shouldScrollBeSmooth) {
            binding.profileInfoRecyclerView.smoothScrollToPosition(0)
        } else {
            binding.profileInfoRecyclerView.scrollToPosition(0)
        }
    }

    private fun isTextInputEditTextNotEmpty() =
        binding.searchProfileTextInputEditText.text.toString().isNotEmpty()

    fun bindIdlingResource(receivedCountingIdlingResource: CountingIdlingResource) {
        countingIdlingResource = receivedCountingIdlingResource
    }

    private fun provideRecyclerViewLayoutManager() =
        castTo<LinearLayoutManager>(binding.profileInfoRecyclerView.layoutManager)

    private fun changeViewState(adapterPosition: Int, viewState: Int) {
        if (adapterPosition == headerAdapter) {
            (castTo<HeaderAdapter>(concatAdapter.adapters[adapterPosition]))?.apply {
                updateViewState(viewState)
                viewModel.saveValueToDataStore(
                    viewModel.obtainValueFromDataStore().toBuilder().setIsHeaderVisible(true)
                        .build()
                )
            }
        } else {
            (castTo<TransientViewsAdapter>(concatAdapter.adapters[adapterPosition]))?.apply {
                updateViewState(viewState)
                notifyDataSetChanged()

                when (viewState) {
                    endOfResults -> saveItemView(isEndOfResultsItemVisible = true)
                    loading -> saveItemView(isPaginationLoadingItemVisible = true)
                    retry -> saveItemView(isRetryItemVisible = true)
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
            saveValueToDataStore(
                obtainValueFromDataStore().toBuilder()
                    .setIsEndOfResultsViewVisible(isEndOfResultsItemVisible).build()
            )
            saveValueToDataStore(
                obtainValueFromDataStore().toBuilder()
                    .setIsPaginationLoadingViewVisible(isPaginationLoadingItemVisible).build()
            )
            saveValueToDataStore(
                obtainValueFromDataStore().toBuilder().setIsRetryViewVisible(isRetryItemVisible)
                    .build()
            )
        }
    }

    private fun shouldRecyclerViewAnimationBeExecuted(): Boolean {
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
        castTo<T>(concatAdapter.adapters[adapterPosition])

    private fun handleConnectionState(
        onConnectionAvailable: () -> Unit = {},
        onConnectionUnavailable: () -> Unit = {}
    ) {
        if (viewModel.obtainConnectionState() == Available) {
            onConnectionAvailable()
        } else {
            onConnectionUnavailable()
        }
    }

    private fun launchBrowser(profileUrl: String) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(profileUrl)
            )
        )
    }

    companion object {
        private const val headerAdapter = 0
        private const val githubProfileAdapter = 1
        private const val transientViewsAdapter = 2
    }
}
