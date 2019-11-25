package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.view

import android.content.Intent
import android.os.Bundle
import android.view.View.*
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.adapter.GithubProfileAdapter
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.repository.GithubProfileInformationRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.viewModel.GithubProfileInfoObtainmentViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.viewModel.GithubProfileInfoObtainmentViewModelFactory
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.showRepositoryInfo.view.ShowRepositoryInfoActivity
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.ActivityFlow
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.cellular
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.disconnected
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.forbidden
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.unknownHostException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.wifi
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.customViews.snackBar.CustomSnackBar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.applyViewVisibility
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.changeDrawable
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.hideKeyboard
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.showSnackBar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.interfaces.OnItemClicked
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.network.NetworkChecking
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.offline_layout.view.*

class GithubProfileInfoObtainmentActivity :
    AppCompatActivity(R.layout.activity_main),
    ActivityFlow {

    private val githubUserAdapter = GithubProfileAdapter()
    private var customSnackBar: CustomSnackBar? = null
    private var hasUserRequestedAnotherResultPage = false
    private var shouldRecyclerViewAnimationBeExecuted = true
    private var previousErrorMessage = 0
    private var hasBackToTopButtonBeenClicked = false

    private val viewModel: GithubProfileInfoObtainmentViewModel by lazy {
        ViewModelProvider(
            this, GithubProfileInfoObtainmentViewModelFactory(
                GithubProfileInformationRepository()
            )
        ).get(GithubProfileInfoObtainmentViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        handleViewModel()
        setupExtras()
    }

    override fun setupView() {

        //Condition when users rotate the screen and the activity gets destroyed
        if (viewModel.isThereAnOngoingCall) {
            applyViewVisibility(repositoryLoadingProgressBar, VISIBLE)
            setupUpperViewsInteraction(false)
            viewModel.shouldASearchBePerformed = false
            changeDrawable(actionIconImageView, R.drawable.ic_close)
        }

        if (viewModel.hasFirstSuccessfulCallBeenMade && !viewModel.isThereAnyProfileToBeSearched)
            changeDrawable(
                actionIconImageView,
                R.drawable.ic_close
            )

        if (viewModel.haveUsersHadAnyTroubleDuringTheFirstCall) applyViewVisibility(
            offlineLayout,
            VISIBLE
        )

        if (viewModel.lastVisibleListItem >= 10) applyViewVisibility(backToTopButton, VISIBLE)

        customSnackBar = CustomSnackBar.make(this.findViewById(android.R.id.content))

        backToTopButton.setOnClickListener {
            if (backToTopButton.visibility != GONE) applyViewVisibility(
                backToTopButton,
                INVISIBLE
            )
            hasBackToTopButtonBeenClicked = true
            profileInfoRecyclerView.smoothScrollToPosition(0)
        }

        profileInfoRecyclerView.apply {
            setHasFixedSize(true)
            adapter = githubUserAdapter
            setupRecyclerViewAddOnScrollListener()
        }

        actionIconImageView.setOnClickListener {
            handleActionIconClick()
        }

        setupTextInputEditText()

        githubProfileListSwipeRefreshLayout.setOnRefreshListener {
            viewModel.hasAnyUserRequestedUpdatedData = true
            searchProfile(null, true)
        }

        githubUserAdapter.setOnItemClicked(object :
            OnItemClicked {

            override fun onItemClick(adapterPosition: Int, id: Int) {
                when (id) {
                    Constants.githubProfileRecyclerViewCell -> {
                        when (NetworkChecking.checkIfInternetConnectionIsAvailable(
                            applicationContext
                        )) {

                            cellular, wifi -> {
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
                            }

                            disconnected -> showInternetConnectionStatusSnackBar(false)
                        }
                    }
                }
            }
        })

        offlineLayout.retryButton.setOnClickListener {
            if (offlineLayout.visibility == VISIBLE) applyViewVisibility(offlineLayout, GONE)
            if (!isFieldEmpty())
                searchProfile(isFieldEmpty = false, shouldTheListItemsBeRemoved = true)
            else
                searchProfile(isFieldEmpty = true, shouldTheListItemsBeRemoved = true)
        }
    }

    override fun handleViewModel() {

        //Success LiveData
        viewModel.successLiveData.observe(this, Observer { githubUsersList ->

            setupUpperViewsInteraction(true)
            viewModel.shouldASearchBePerformed = false
            applyViewVisibility(githubProfileListSwipeRefreshLayout)

            if (offlineLayout.visibility != GONE) {
                applyViewVisibility(offlineLayout, GONE)
                viewModel.haveUsersHadAnyTroubleDuringTheFirstCall = false
            }

            if (viewModel.hasAnyUserRequestedUpdatedData) {
                applyViewVisibility(githubProfileListSwipeRefreshLayout)
                githubUserAdapter.updateDataSource(githubUsersList)
                viewModel.hasAnyUserRequestedUpdatedData = false
            }

            if (!viewModel.hasFirstSuccessfulCallBeenMade)
                githubUserAdapter.updateDataSource(githubUsersList)
            else {
                githubUserAdapter.updateDataSource(githubUsersList)
                profileInfoRecyclerView.adapter?.notifyDataSetChanged()
                applyViewVisibility(repositoryLoadingProgressBar, GONE)
            }

            if (shouldRecyclerViewAnimationBeExecuted)
                runLayoutAnimation(profileInfoRecyclerView)

            if (!shouldRecyclerViewAnimationBeExecuted)
                shouldRecyclerViewAnimationBeExecuted = true
        })

        //Error LiveData
        viewModel.errorSingleImmutableLiveDataEvent.observe(this, Observer { state ->

            if (!shouldRecyclerViewAnimationBeExecuted) shouldRecyclerViewAnimationBeExecuted = true
            setupUpperViewsInteraction(true)
            viewModel.shouldASearchBePerformed = true
            if (state.first != forbidden) changeDrawable(actionIconImageView, R.drawable.ic_search)

            when (state.first) {
                unknownHostException -> {
                    //It needs to be checked otherwise, the app could end up replacing a list full of items
                    // with the offline layout
                    if (viewModel.hasFirstSuccessfulCallBeenMade)
                        showErrorMessages(state.second, false)
                    else
                        showErrorMessages(state.second, true)
                }

                else -> showErrorMessages(state.second, false)
            }
        })
    }

    override fun setupExtras() {

        when (NetworkChecking.checkIfInternetConnectionIsAvailable(applicationContext)) {
            disconnected -> showInternetConnectionStatusSnackBar(false)
        }

        setupInternetConnectionObserver()
    }

    private fun searchProfile(
        isFieldEmpty: Boolean? = null,
        shouldTheListItemsBeRemoved: Boolean? = null
    ) {

        hideKeyboard(searchProfileTextInputEditText)

        //Since the SwipeRefreshLayout is only visible when at least one call has already been made, even if users delete the text from the
        //TextInputEditText, users will already have typed something onto it and this string will be stored in the ViewModel as soon as they search for it, so,
        //it can be used to either refresh the list or to keep getting more pages from the Github API. It does not apply to the "ActionIcon" when it is a Magnifying glass icon
        // or when users search using the Keyboard Magnifying Glass icon, in this case, there has to be some text on the TextInputEditText
        if (viewModel.hasAnyUserRequestedUpdatedData) {
            callApi {
                viewModel.getGithubProfileList(
                    null,
                    shouldTheListItemsBeRemoved
                )
            }
        } else if (hasUserRequestedAnotherResultPage && !viewModel.isTheNumberOfItemsOfTheLastCallLessThanTwenty) {
            /** ${viewModel.isTheNumberOfItemsOfTheLastCallLessThanTwenty} being false basically means it's the last page and
             * there's no need to perform another call because one has reached the end of the list */
            callApi {
                viewModel.getGithubProfileList(
                    null,
                    shouldTheListItemsBeRemoved
                )
            }
        }

        isFieldEmpty?.let {
            if (!it) callApi {
                viewModel.getGithubProfileList(
                    searchProfileTextInputEditText.text.toString(),
                    shouldTheListItemsBeRemoved
                )
            }
            else {
                showSnackBar(
                    this,
                    getString(R.string.empty_field_error)
                )

                viewModel.hasAnyUserRequestedUpdatedData = false
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
            scheduleLayoutAnimation()

            if (viewModel.successfulCallsCount == 1) viewModel.hasFirstSuccessfulCallBeenMade = true

            applyViewVisibility(githubProfileListSwipeRefreshLayout)

            if (repositoryLoadingProgressBar.visibility == VISIBLE)
                applyViewVisibility(repositoryLoadingProgressBar, GONE)
        }
    }

    private fun callApi(genericFunction: () -> Unit) {

        when (NetworkChecking.checkIfInternetConnectionIsAvailable(applicationContext)) {

            cellular, wifi -> {
                if (!githubProfileListSwipeRefreshLayout.isRefreshing)
                    applyViewVisibility(repositoryLoadingProgressBar, VISIBLE)
                setupUpperViewsInteraction(false)
                viewModel.shouldASearchBePerformed = false
                changeDrawable(actionIconImageView, R.drawable.ic_close)
                genericFunction.invoke()
                viewModel.isThereAnOngoingCall = true
            }

            disconnected -> showInternetConnectionStatusSnackBar(false)
        }
    }

    private fun showErrorMessages(message: Int, shouldOfflineLayoutBeShown: Boolean) {
        if (shouldOfflineLayoutBeShown) {
            if (offlineLayout.visibility != VISIBLE) {
                applyViewVisibility(offlineLayout, VISIBLE)
                if (viewModel.successfulCallsCount == 0 && viewModel.unsuccessfulCallsCount == 1)
                    viewModel.haveUsersHadAnyTroubleDuringTheFirstCall = true
            }
        }
        applyViewVisibility(repositoryLoadingProgressBar, GONE)
        applyViewVisibility(githubProfileListSwipeRefreshLayout)

        if (viewModel.hasLastCallBeenSuccessful)
            showSnackBar(this, getString(message))
        else {
            if (previousErrorMessage != message) {
                previousErrorMessage = message
                showSnackBar(this, getString(message))
            }
        }
    }

    private fun isFieldEmpty() = searchProfileTextInputEditText.text.toString().isEmpty()

    private fun setupInternetConnectionObserver() {
        NetworkChecking.internetConnectionAvailabilityObservable(applicationContext)
            .observe(this, Observer { isInternetAvailable ->
                when (isInternetAvailable) {
                    true -> showInternetConnectionStatusSnackBar(true)
                    false -> showInternetConnectionStatusSnackBar(false)
                }
            })
    }

    private fun showInternetConnectionStatusSnackBar(isInternetConnectionAvailable: Boolean) {
        with(customSnackBar) {
            if (isInternetConnectionAvailable) {
                this?.setText(getString(R.string.back_online_success_message))?.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.green_700
                    )
                )
                this?.dismiss()
            } else {
                this?.setText(getString(R.string.no_connection_error))?.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.red_700
                    )
                )
                this?.show()
            }
        }
    }

    private fun handleActionIconClick() {
        if (offlineLayout.visibility == VISIBLE) applyViewVisibility(
            offlineLayout,
            GONE
        )
        if (!viewModel.isThereAnOngoingCall) {
            if (viewModel.shouldASearchBePerformed) {
                if (!isFieldEmpty()) {
                    searchProfile(isFieldEmpty = false, shouldTheListItemsBeRemoved = true)
                } else
                    searchProfile(isFieldEmpty = true, shouldTheListItemsBeRemoved = true)
            } else {
                searchProfileTextInputEditText.setText("")
                changeDrawable(actionIconImageView, R.drawable.ic_search)
                if (!viewModel.shouldASearchBePerformed) viewModel.shouldASearchBePerformed = true
            }
        }
    }

    private fun setupTextInputEditText() {
        with(searchProfileTextInputEditText) {
            requestFocus()
            setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (!isFieldEmpty()) {
                        searchProfile(isFieldEmpty = false, shouldTheListItemsBeRemoved = true)
                    }
                    return@OnEditorActionListener true
                }
                false
            })

            addTextChangedListener {
                doOnTextChanged { _, _, _, _ ->

                    if (!viewModel.shouldASearchBePerformed) viewModel.shouldASearchBePerformed =
                        true

                    changeDrawable(
                        actionIconImageView,
                        R.drawable.ic_search
                    )
                }
            }
        }
    }

    private fun setupRecyclerViewAddOnScrollListener() {
        profileInfoRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val total = recyclerView.layoutManager?.itemCount
                val currentLastItem =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()

                viewModel.lastVisibleListItem = currentLastItem

                if (currentLastItem in 4..9)
                    if (backToTopButton.visibility != GONE) {
                        applyViewVisibility(backToTopButton, GONE)
                        hasBackToTopButtonBeenClicked = false
                    }

                if (currentLastItem >= 10 && backToTopButton.visibility != VISIBLE && !hasBackToTopButtonBeenClicked)
                    applyViewVisibility(
                        backToTopButton,
                        VISIBLE
                    )

                if (currentLastItem == total?.minus(1)) {
                    //This attribute was created to avoid making an API call twice or more because sometimes this callback is called more than once, so,
                    //the API call method won't be called until the previous API call finishes combining it with the 'isThereAnOngoingCall' attribute located in the
                    //GithubProfileInfoObtainmentViewModel
                    hasUserRequestedAnotherResultPage = true
                    shouldRecyclerViewAnimationBeExecuted = false
                    if (hasUserRequestedAnotherResultPage && !viewModel.isThereAnOngoingCall && !viewModel.hasAnyUserRequestedUpdatedData) {
                        searchProfile(
                            isFieldEmpty = null,
                            shouldTheListItemsBeRemoved = false
                        )
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

    override fun onDestroy() {
        super.onDestroy()
        viewModel.isThereAnyProfileToBeSearched =
            searchProfileTextInputEditText.text.toString().isEmpty()
    }
}