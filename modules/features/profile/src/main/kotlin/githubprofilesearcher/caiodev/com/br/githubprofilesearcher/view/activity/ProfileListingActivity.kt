package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.cast.ValueCasting.castTo
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.types.number.defaultInteger
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.types.string.emptyString
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.profile.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.profile.databinding.ActivityProfileListingBinding
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.snackBar.applyViewVisibility
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.snackBar.hideKeyboard
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.snackBar.runTaskOnBackground
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.snackBar.showMessage
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.adapter.HeaderAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.adapter.ProfileAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.adapter.TransientViewsAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.state.ProfileUIState
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.viewHolder.OnItemSelectedListener
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.viewHolder.transientItemViews.LoadingViewHolder.Companion.LOADING
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.viewModel.ProfileViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.R as Core

internal class ProfileListingActivity : ComponentActivity() {
    private lateinit var binding: ActivityProfileListingBinding

    private val snackBar by lazy {
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

    private fun setupView() {
        binding = ActivityProfileListingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionViews()
        setupRecyclerView()
        setupTextInputEditText()
        initializeAdapterCallback()
        setupObserver()
    }

    private fun setupObserver() {
        runTaskOnBackground {
            viewModel.uiState.collect { uiState ->
                binding.progressBar.isVisible = uiState.isLoading
                if (uiState.isSuccess) {
                    if (uiState.isSuccessWithContent) {
                        onSuccess(uiState = uiState)
                    } else {
                        snackBar.showMessage(uiState.successMessage)
                    }
                } else {
                    Unit
                }
            }
        }
    }

    private fun setupActionViews() {
        binding.backToTopButton.setOnClickListener {
            it.isClickable = false
            it.applyViewVisibility(INVISIBLE)
            binding.profileInfoRecyclerView.scrollToPosition(defaultInteger())
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
                        makeTextInputEditTextNotEmptyRequiredCall()
                        return@OnEditorActionListener true
                    }
                    false
                },
            )
        }
    }

    private fun initializeAdapterCallback() {
        provideAdapter<TransientViewsAdapter>(TRANSIENT_VIEWS_ADAPTER)
            ?.setOnItemClicked { _, _ -> getRecyclerData() }
    }

    private fun obtainProfileListener(): OnItemSelectedListener {
        return object : OnItemSelectedListener {
            override fun onItemSelected(text: String) {
                launchBrowser(text)
            }
        }
    }

    private fun onSuccess(uiState: ProfileUIState) {
        binding.progressBar.isVisible = false
        setupUpperViewsInteraction(true)

        provideAdapter<ProfileAdapter>(PROFILE_ADAPTER)?.apply {
            updateDataSource(uiState.content)
            notifyDataSetChanged()
        }
    }

    private fun getTextFieldData(profile: String = emptyString()) {
        getData { viewModel.getData(profile) }
    }

    private fun getRecyclerData() {
        changeViewState(TRANSIENT_VIEWS_ADAPTER, LOADING)
        getData { viewModel.paginateData() }
    }

    private fun makeTextInputEditTextNotEmptyRequiredCall() {
        binding.searchProfileTextInputEditText.hideKeyboard()
        if (isTextInputEditTextNotEmpty()) {
            binding.progressBar.applyViewVisibility(VISIBLE)
            getTextFieldData(binding.searchProfileTextInputEditText.text.toString())
        } else {
            snackBar.showMessage(
                Core.string.empty_field,
            )
        }
    }

    private inline fun getData(crossinline task: () -> Unit) {
        task()
    }

    private fun setupRecyclerViewAddOnScrollListener() {
        binding.profileInfoRecyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(
                    recyclerView: RecyclerView,
                    newState: Int,
                ) {
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
                }
            },
        )
    }

    private fun setupUpperViewsInteraction(isInteractive: Boolean) {
        with(binding.searchProfileTextInputEditText) {
            isClickable = isInteractive
            isCursorVisible = isInteractive
            isFocusable = isInteractive
            isFocusableInTouchMode = isInteractive
            isLongClickable = isInteractive
            if (isInteractive) requestFocus()
        }
    }

    private fun isTextInputEditTextNotEmpty() = binding.searchProfileTextInputEditText.text.toString().isNotEmpty()

    private fun provideRecyclerViewLayoutManager() = castTo<LinearLayoutManager>(binding.profileInfoRecyclerView.layoutManager)

    private fun changeViewState(
        adapterPosition: Int,
        viewState: Int,
    ) {
        if (adapterPosition == HEADER_ADAPTER) {
            (castTo<HeaderAdapter>(concatAdapter.adapters[adapterPosition]))?.apply {
                updateViewState(viewState)
            }
        } else {
            (castTo<TransientViewsAdapter>(concatAdapter.adapters[adapterPosition]))?.apply {
                updateViewState(viewState)
                notifyDataSetChanged()
            }
        }
    }

    private inline fun <reified T> provideAdapter(adapterPosition: Int) = castTo<T>(concatAdapter.adapters[adapterPosition])

    private fun launchBrowser(profileUrl: String) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(profileUrl),
            ),
        )
    }

    companion object {
        private const val HEADER_ADAPTER = 0
        private const val PROFILE_ADAPTER = 1
        private const val TRANSIENT_VIEWS_ADAPTER = 2
    }
}
