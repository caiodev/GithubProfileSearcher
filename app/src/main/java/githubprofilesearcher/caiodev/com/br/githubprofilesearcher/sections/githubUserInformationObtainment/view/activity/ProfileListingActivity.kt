package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.activity

import android.content.res.Configuration
import android.os.Bundle
import android.view.View.*
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.idling.CountingIdlingResource
import com.google.android.material.snackbar.Snackbar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.databinding.ActivityProfileListingBinding
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.UserProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.adapter.GithubProfileAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.adapter.HeaderAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.adapter.TransientViewsAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.viewHolder.OnItemSelectedListener
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel.ProfileViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel.ProfileViewModel.Companion.emptyString
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.userRepositoryInformationObtainment.view.GithubProfileDetailActivity
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.LifecycleOwnerFlow
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.OnItemClicked
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.*
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.cast.ValueCasting.castTo
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.empty
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.endOfResults
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.githubProfileAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.header
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.headerAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.loading
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.retry
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.transientViewsAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.customViews.snackBar.CustomSnackBar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.*
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileListingActivity : AppCompatActivity(), LifecycleOwnerFlow {

    private lateinit var binding: ActivityProfileListingBinding
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

    private val viewModel by viewModel<ProfileViewModel>()

    private val concatAdapter by lazy {
        ConcatAdapter(
            HeaderAdapter(R.string.github_user_list_header),
            GithubProfileAdapter(obtainProfileListener()),
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
        bindViewModelDataToUIInCaseOfOrientationChanges()
        setupActionViews()
        setupSwipeRefreshLayout()
        setupRecyclerView()
        setupTextInputEditText()
        initializeAdapterCallback()
    }

    private fun bindViewModelDataToUIInCaseOfOrientationChanges() {
        if (viewModel.isObserverUnchanged()) {
            viewModel.saveValueToDataStore()
            viewModel.updateUIWithCache()
            changeViewState(headerAdapter, header)
            if (viewModel.obtainValueFromDataStore().isRetryViewVisible) {
                changeViewState(transientViewsAdapter, retry)
            }
        } else {
            if (viewModel.obtainValueFromDataStore().profile.isNotEmpty()) {
                binding.searchProfileTextInputEditText.setText(
                    viewModel.obtainValueFromDataStore().profile
                )
                viewModel.saveValueToDataStore(
                    viewModel.obtainValueFromDataStore().toBuilder()
                        .setShouldASearchBePerformed(true).build()
                )
            }

            changeViewState(headerAdapter, header)

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
                if (hasASuccessfulCallAlreadyBeenMade &&
                    !isTextInputEditTextNotEmpty && !hasUserDeletedProfileText
                ) {
                    binding.actionIconImageView.changeDrawable(R.drawable.ic_close)
                }
            }

            provideRecyclerViewLayoutManager()?.findFirstVisibleItemPosition()?.let {
                if (it >= 2) {
                    binding.backToTopButton.applyViewVisibility(VISIBLE)
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
            if (!viewModel.obtainValueFromDataStore().hasASuccessfulCallAlreadyBeenMade
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
                        startActivity(
                            GithubProfileDetailActivity.newIntent(
                                applicationContext,
                                text
                            )
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

    override fun handleViewModel() {
        onSuccess()
        onError()
    }

    private fun onSuccess() {
        runTaskOnBackground {
            viewModel.successStateFlow.collect { content ->
                if (!viewModel.isResultListEmpty()) {
                    viewModel.saveValueToDataStore(
                        viewModel.obtainValueFromDataStore().toBuilder()
                            .setHasLastCallBeenUnsuccessful(false).build()
                    )
                    viewModel.saveValueToDataStore(
                        viewModel.obtainValueFromDataStore().toBuilder()
                            .setIsThereAnOngoingCall(false)
                            .build()
                    )

                    if (this::countingIdlingResource.isInitialized) {
                        countingIdlingResource.decrement()
                    }

                    setupUpperViewsInteraction(true)

                    if (viewModel.obtainValueFromDataStore().hasUserDeletedProfileText &&
                        viewModel.obtainValueFromDataStore().profile.isNotEmpty()
                    ) {
                        viewModel.saveValueToDataStore(
                            viewModel.obtainValueFromDataStore().toBuilder()
                                .setShouldASearchBePerformed(true).build()
                        )
                    } else {
                        viewModel.saveValueToDataStore(
                            viewModel.obtainValueFromDataStore().toBuilder()
                                .setShouldASearchBePerformed(false).build()
                        )
                    }

                    if (!viewModel.obtainValueFromDataStore().isHeaderVisible) {
                        changeViewState(headerAdapter, header)
                    }

                    castTo<List<UserProfileInformation>>(content)?.let {
                        splitOnSuccess(it)
                    }
                }
            }
        }
    }

    private fun splitOnSuccess(githubUsersList: List<UserProfileInformation>) {
        if (viewModel.obtainValueFromDataStore().numberOfItems < ProfileViewModel.numberOfItemsPerPage) {
            changeViewState(transientViewsAdapter, endOfResults)
        } else {
            changeViewState(transientViewsAdapter, empty)
        }

        provideAdapter<GithubProfileAdapter>(githubProfileAdapter)?.apply {
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
            provideAdapter<GithubProfileAdapter>(githubProfileAdapter)?.updateDataSource(
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
            viewModel.errorStateFlow.collect { error ->
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

                setupUpperViewsInteraction(true)

                binding.githubProfileListSwipeRefreshLayout.applySwipeRefreshVisibilityAttributes(
                    isSwipeEnabled = true
                )

                binding.actionIconImageView.changeDrawable(R.drawable.ic_search)

                when (error) {
                    UnknownHost, SocketTimeout, Connect ->
                        showErrorMessages(R.string.unknown_host_exception_and_socket_timeout_exception)
                    SSLHandshake -> showErrorMessages(R.string.ssl_handshake_exception)
                    ClientSide -> showErrorMessages(R.string.client_side_error)
                    ServerSide -> showErrorMessages(R.string.server_side_error)
                    Forbidden -> showErrorMessages(R.string.api_query_limit_exceeded_error)
                    else -> showErrorMessages(R.string.generic_exception_and_generic_error)
                }

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
                showErrorMessages(R.string.no_connection_error)
            }
        )
    }

    private fun showErrorMessages(@StringRes message: Int) {
        binding.searchProfileTextInputEditText.hideKeyboard()
        binding.repositoryLoadingProgressBar.applyViewVisibility(GONE)
        setupUpperViewsInteraction(true)
        errorSnackBar.showErrorSnackBar(
            message,
            onDismissed = {
                shouldRecallInternetConnectivitySnackBar()
            }
        )
    }

    private fun shouldRecallInternetConnectivitySnackBar(): Any {
        if (!viewModel.obtainValueFromDataStore().isRetryViewVisible) {
            return handleConnectionState(
                onConnectionUnavailable = {
                    internetConnectivitySnackBar.showInternetConnectionStatusSnackBar(false)
                }
            )
        }
        return emptyString
    }

    private fun setupInternetConnectionObserver() {
        runTaskOnBackground {
            viewModel.provideConnectionObserver().collect { connectionState ->
                if (connectionState == ConnectionAvailable) {
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
                } else {
                    internetConnectivitySnackBar.showInternetConnectionStatusSnackBar(false)
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
                binding.searchProfileTextInputEditText.setText(emptyString)
                binding.actionIconImageView.changeDrawable(R.drawable.ic_search)
                if (!viewModel.obtainValueFromDataStore().shouldASearchBePerformed
                ) viewModel.saveValueToDataStore(
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
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
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
        if (viewModel.obtainConnectionState() == ConnectionAvailable) {
            onConnectionAvailable()
        } else {
            onConnectionUnavailable()
        }
    }
}
