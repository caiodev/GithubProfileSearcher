package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.profile.ui.activity

import android.R
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.profile.ui.viewModel.ProfileViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.databinding.ActivityProfileListingBinding
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.extensions.ui.applyViewVisibility
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.extensions.ui.hideKeyboard
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.extensions.ui.runTaskOnBackground
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.extensions.ui.showMessage
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.feature.profile.adapter.ProfileAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.feature.profile.uiState.ProfileUIState
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.feature.profile.viewHolder.OnItemSelectedListener
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.utils.cast.castTo
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.utils.types.number.defaultInteger
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.utils.types.string.emptyString
import org.koin.androidx.viewmodel.ext.android.viewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.resources.R as Resources
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.R as UI

class ProfileListingActivity : ComponentActivity() {
    private lateinit var binding: ActivityProfileListingBinding

    private val snackBar by lazy {
        Snackbar.make(
            findViewById(R.id.content),
            Resources.string.generic,
            Snackbar.LENGTH_SHORT,
        )
    }

    private val viewModel by viewModel<ProfileViewModel>()

    private val profileAdapter by lazy {
        ProfileAdapter(obtainProfileListener())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(UI.style.AppTheme)
        super.onCreate(savedInstanceState)
        setupView()
    }

    private fun setupView() {
        binding = ActivityProfileListingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionViews()
        setupRecyclerView()
        setupTextInputEditText()
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
                    if (uiState.content.isNotEmpty()) {
                        onSuccess(uiState = uiState)
                    }
                    Toast
                        .makeText(
                            applicationContext,
                            getString(uiState.errorMessage),
                            Toast.LENGTH_LONG,
                        )
                        .show()
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
            adapter = profileAdapter
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

    private fun obtainProfileListener(): OnItemSelectedListener =
        object : OnItemSelectedListener {
            override fun onItemSelected(text: String) {
                launchBrowser(text)
            }
        }

    private fun onSuccess(uiState: ProfileUIState) {
        binding.progressBar.isVisible = false
        setupUpperViewsInteraction(true)
        profileAdapter.apply {
            updateDataSource(uiState.content)
            notifyDataSetChanged()
        }
    }

    private fun getTextFieldData(profile: String = emptyString()) {
        viewModel.getData(profile)
    }

    private fun makeTextInputEditTextNotEmptyRequiredCall() {
        binding.searchProfileTextInputEditText.hideKeyboard()
        if (isTextInputEditTextNotEmpty()) {
            binding.progressBar.applyViewVisibility(VISIBLE)
            getTextFieldData(
                binding.searchProfileTextInputEditText.text
                    .toString()
                    .trim(),
            )
        } else {
            snackBar.showMessage(Resources.string.empty_field)
        }
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
                                if (visibility != INVISIBLE) applyViewVisibility(INVISIBLE)
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

    private fun isTextInputEditTextNotEmpty() =
        binding.searchProfileTextInputEditText.text
            .toString()
            .isNotEmpty()

    private fun provideRecyclerViewLayoutManager() =
        binding.profileInfoRecyclerView.layoutManager.castTo<LinearLayoutManager>()

    private fun launchBrowser(profileUrl: String) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(profileUrl),
            ),
        )
    }
}
