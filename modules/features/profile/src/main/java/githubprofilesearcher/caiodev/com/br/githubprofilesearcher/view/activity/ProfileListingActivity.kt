package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.contracts.OnItemClicked
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.cast.ValueCasting.castTo
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.types.string.obtainDefaultString
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.keyValue.ProfileKeyValueIDs
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.profile.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.profile.databinding.ActivityProfileListingBinding
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.snackBar.applySwipeRefreshVisibilityAttributes
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.snackBar.applyViewVisibility
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.snackBar.changeDrawable
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.snackBar.hideKeyboard
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.snackBar.runTaskOnBackground
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.snackBar.showErrorSnackBar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.states.Empty
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.states.Error
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.states.Loading
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.adapter.HeaderAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.adapter.ProfileAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.adapter.TransientViewsAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.states.User
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.viewHolder.OnItemSelectedListener
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.viewHolder.transientItemViews.EndOfResultsViewHolder.Companion.endOfResults
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.viewHolder.transientItemViews.LoadingViewHolder.Companion.loading
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.viewHolder.transientItemViews.RetryViewHolder.Companion.retry
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.viewModel.ProfileViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.R as Core

internal class ProfileListingActivity : ComponentActivity() {

    private lateinit var binding: ActivityProfileListingBinding

    private val errorSnackBar by lazy {
        Snackbar.make(
            findViewById(android.R.id.content),
            Core.string.generic,
            Snackbar.LENGTH_SHORT,
        )
    }

    private val viewModel by viewModel<ProfileViewModel>()

    private val concatAdapter by lazy {
        ConcatAdapter(
            HeaderAdapter(R.string.user_list_header),
            ProfileAdapter(obtainProfileListener()),
            TransientViewsAdapter(),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setupView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        viewModel.setValue(key = ProfileKeyValueIDs.UserInputStatus, value = isTextInputEditTextNotEmpty())

        if (isTextInputEditTextNotEmpty()) {
            viewModel.setValue(
                key = ProfileKeyValueIDs.ProfileText,
                value = binding.searchProfileTextInputEditText.text.toString(),
            )
        }
    }

    private fun setupView() {
        binding = ActivityProfileListingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupSwipeRefreshLayout()
        setupActionViews()
        setupRecyclerView()
        setupTextInputEditText()
        initializeAdapterCallback()
        setupObserver()
    }

    private fun setupObserver() {
        runTaskOnBackground {
            viewModel.uiState.collect { uiState ->
                when (uiState) {
                    is User -> onSuccess(uiState)
                    is Error -> showErrorMessage(uiState.message)
                    is Loading -> binding.repositoryLoadingProgressBar.isVisible = true
                    is Empty -> {}
                    else -> Unit
                }
            }
        }
    }

    private fun setupActionViews() {
        binding.actionIconImageView.setOnClickListener {
            handleActionIconClick()
        }

        binding.backToTopButton.setOnClickListener {
            it.isClickable = false
            it.applyViewVisibility(INVISIBLE)
            binding.profileInfoRecyclerView.scrollToPosition(0)
        }
    }

    private fun setupSwipeRefreshLayout() {
        val isLocalPopulation: Boolean = viewModel.getValue(key = ProfileKeyValueIDs.LocalPopulationStatus)
        val hasASuccessfulCallAlreadyBeenMade: Boolean = viewModel.getValue(key = ProfileKeyValueIDs.SuccessStatus)

        binding.githubProfileListSwipeRefreshLayout.apply {
            if (!isLocalPopulation && !hasASuccessfulCallAlreadyBeenMade) {
                applySwipeRefreshVisibilityAttributes(isSwipeEnabled = false)
            }

            setOnRefreshListener {
                viewModel.setValue(key = ProfileKeyValueIDs.DataRequestStatus, value = true)
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
                },
            )

            addTextChangedListener {
                doOnTextChanged { text, _, _, _ ->
                    val shouldASearchBePerformed: Boolean = viewModel.getValue(ProfileKeyValueIDs.SearchStatus)
                    if (!shouldASearchBePerformed) {
                        viewModel.setValue(key = ProfileKeyValueIDs.SearchStatus, value = true)
                    }

                    binding.actionIconImageView.changeDrawable(R.drawable.ic_search)

                    text?.let {
                        if (it.isEmpty()) {
                            viewModel.setValue(key = ProfileKeyValueIDs.DeletedProfileStatus, value = true)
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
            },
        )
    }

    private fun obtainProfileListener(): OnItemSelectedListener {
        return object : OnItemSelectedListener {
            override fun onItemSelected(text: String) {
                launchBrowser(text)
            }
        }
    }

    private fun onSuccess(user: User) {
        setupUpperViewsInteraction(true)

        val isHeaderVisible: Boolean = viewModel.getValue(key = ProfileKeyValueIDs.HeaderStatus)

        if (!isHeaderVisible) {
            changeViewState(headerAdapter, HeaderAdapter.header)
        }

        provideAdapter<ProfileAdapter>(githubProfileAdapter)?.apply {
            updateDataSource(user.content)
            notifyDataSetChanged()
        }

        if (viewModel.getValue(key = ProfileKeyValueIDs.DataRequestStatus)) {
            viewModel.setValue(key = ProfileKeyValueIDs.DataRequestStatus, value = false)
        }

        if (viewModel.getValue(key = ProfileKeyValueIDs.DataRequestStatus)) {
            provideAdapter<ProfileAdapter>(githubProfileAdapter)?.updateDataSource(
                user.content,
            )

            viewModel.setValue(key = ProfileKeyValueIDs.DataRequestStatus, value = false)
        }
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
            viewModel.setValue(key = ProfileKeyValueIDs.DataRequestStatus, value = true)
            viewModel.setValue(key = ProfileKeyValueIDs.DeletedProfileStatus, value = false)
            updatedProfileCall(binding.searchProfileTextInputEditText.text.toString())
        } else {
            errorSnackBar.showErrorSnackBar(
                Core.string.empty_field,
            )
        }
    }

    private inline fun callApiThroughViewModel(crossinline task: () -> Unit) {
        viewModel.setValue(key = ProfileKeyValueIDs.CallStatus, value = true)

        val hasUserDeletedProfileText: Boolean =
            viewModel.getValue(key = ProfileKeyValueIDs.DeletedProfileStatus)
        if (!hasUserDeletedProfileText) {
            setupUpperViewsInteraction(false)
            binding.actionIconImageView.changeDrawable(R.drawable.ic_close)
        }

        viewModel.setValue(key = ProfileKeyValueIDs.SearchStatus, value = false)

        task()
    }

    private fun showErrorMessage(@StringRes message: Int) {
        viewModel.setValue(key = ProfileKeyValueIDs.LastAttemptStatus, value = true)
        viewModel.setValue(key = ProfileKeyValueIDs.CallStatus, value = false)

        if (viewModel.getValue(key = ProfileKeyValueIDs.DataRequestStatus)) {
            viewModel.setValue(key = ProfileKeyValueIDs.DataRequestStatus, value = false)
        }

        binding.githubProfileListSwipeRefreshLayout.applySwipeRefreshVisibilityAttributes()

        binding.actionIconImageView.changeDrawable(R.drawable.ic_search)
        binding.searchProfileTextInputEditText.hideKeyboard()
        binding.repositoryLoadingProgressBar.applyViewVisibility(GONE)
        setupUpperViewsInteraction(true)
        errorSnackBar.showErrorSnackBar(message)

        if (viewModel.getValue(key = ProfileKeyValueIDs.PaginationLoadingStatus)) {
            changeViewState(transientViewsAdapter, retry)
        }
    }

    private fun handleActionIconClick() {
        val isThereAnOngoingCall: Boolean = viewModel.getValue(key = ProfileKeyValueIDs.CallStatus)
        if (!isThereAnOngoingCall) {
            if (viewModel.getValue(key = ProfileKeyValueIDs.SearchStatus)) {
                viewModel.setValue(key = ProfileKeyValueIDs.DataRequestStatus, value = true)
                textInputEditTextNotEmptyRequiredCall()
            } else {
                binding.searchProfileTextInputEditText.setText(obtainDefaultString())
                binding.actionIconImageView.changeDrawable(R.drawable.ic_search)
                viewModel.setValue(key = ProfileKeyValueIDs.SearchStatus, value = true)
                viewModel.setValue(key = ProfileKeyValueIDs.DeletedProfileStatus, value = true)
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
                        viewModel.getValue(key = ProfileKeyValueIDs.SuccessStatus)
                    ) {
                        val isThereAnOngoingCall: Boolean = viewModel.getValue(key = ProfileKeyValueIDs.CallStatus)
                        val isRetryViewVisible: Boolean = viewModel.getValue(key = ProfileKeyValueIDs.RetryStatus)
                        val isEndOfResultsViewVisible: Boolean =
                            viewModel.getValue(key = ProfileKeyValueIDs.EndOfResultsStatus)

                        if (!isThereAnOngoingCall &&
                            !isRetryViewVisible &&
                            !isEndOfResultsViewVisible
                        ) {
                            paginationCall()
                        }
                    }
                }
            },
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

    private fun isTextInputEditTextNotEmpty() =
        binding.searchProfileTextInputEditText.text.toString().isNotEmpty()

    private fun provideRecyclerViewLayoutManager() =
        castTo<LinearLayoutManager>(binding.profileInfoRecyclerView.layoutManager)

    private fun changeViewState(adapterPosition: Int, viewState: Int) {
        if (adapterPosition == headerAdapter) {
            (castTo<HeaderAdapter>(concatAdapter.adapters[adapterPosition]))?.apply {
                updateViewState(viewState)
                viewModel.setValue(key = ProfileKeyValueIDs.HeaderStatus, value = true)
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
        isRetryItemVisible: Boolean = false,
    ) {
        viewModel.apply {
            viewModel.setValue(key = ProfileKeyValueIDs.EndOfResultsStatus, value = isEndOfResultsItemVisible)
            viewModel.setValue(key = ProfileKeyValueIDs.PaginationLoadingStatus, value = isPaginationLoadingItemVisible)
            viewModel.setValue(key = ProfileKeyValueIDs.RetryStatus, value = isRetryItemVisible)
        }
    }

    private inline fun <reified T> provideAdapter(adapterPosition: Int) =
        castTo<T>(concatAdapter.adapters[adapterPosition])

    private fun launchBrowser(profileUrl: String) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(profileUrl),
            ),
        )
    }

    companion object {
        private const val headerAdapter = 0
        private const val githubProfileAdapter = 1
        private const val transientViewsAdapter = 2
    }
}
