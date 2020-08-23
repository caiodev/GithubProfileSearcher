package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.viewHolders.transientItemViews.EmptyViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.viewHolders.transientItemViews.EndOfResultsViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.viewHolders.transientItemViews.LoadingViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.viewHolders.transientItemViews.RetryViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.OnItemClicked
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.empty
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.endOfResults
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.loading
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.retry

class TransientViewsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var itemClicked: OnItemClicked
    private var viewType = empty

    override fun getItemViewType(position: Int) = viewType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val itemView = LayoutInflater.from(parent.context)

        return when (viewType) {

            loading -> {
                LoadingViewHolder(
                    itemView.inflate(
                        R.layout.loading_view_holder_layout,
                        parent,
                        false
                    )
                )
            }

            retry -> {
                RetryViewHolder(
                    itemView.inflate(
                        R.layout.retry_view_holder_layout,
                        parent,
                        false
                    ),
                    itemClicked
                )
            }

            endOfResults -> EndOfResultsViewHolder(
                itemView.inflate(
                    R.layout.end_of_results_view_holder_layout,
                    parent,
                    false
                )
            )

            else ->
                EmptyViewHolder(
                    itemView.inflate(
                        R.layout.empty_view_holder_layout,
                        parent,
                        false
                    )
                )
        }
    }

    override fun getItemCount() = 1

    internal fun updateViewState(newState: Int) {
        viewType = newState
    }

    internal fun setOnItemClicked(onItemClicked: OnItemClicked) {
        itemClicked = onItemClicked
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //
    }
}
