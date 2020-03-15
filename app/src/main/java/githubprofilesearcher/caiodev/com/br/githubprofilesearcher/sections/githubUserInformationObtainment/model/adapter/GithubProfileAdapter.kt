package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.viewTypes.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.viewTypes.Header
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.viewHolders.GithubProfileInformationViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.viewHolders.HeaderViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.viewHolders.transientItemViews.EndOfResultsViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.viewHolders.transientItemViews.LoadingViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.viewHolders.transientItemViews.RetryViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.githubProfileCell
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.header
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.loading
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.retry
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.interfaces.OnItemClicked
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.interfaces.viewTypes.ViewType

class GithubProfileAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var itemClicked: OnItemClicked
    private lateinit var dataSource: List<ViewType>

    override fun getItemCount() = dataSource.size

    override fun getItemViewType(position: Int) = itemViewType(position).provideViewType()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val itemView = LayoutInflater.from(parent.context)

        return when (viewType) {

            header -> HeaderViewHolder(
                itemView.inflate(
                    R.layout.header_view_holder_layout,
                    parent,
                    false
                )
            )

            githubProfileCell -> GithubProfileInformationViewHolder(
                itemView.inflate(
                    R.layout.github_profile_view_holder_layout,
                    parent,
                    false
                ), itemClicked
            )

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
                    )
                    , itemClicked
                )
            }

            else -> EndOfResultsViewHolder(
                itemView.inflate(
                    R.layout.end_of_results_view_holder_layout,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is HeaderViewHolder -> viewHolder.bind(itemViewType(position) as Header)
            is GithubProfileInformationViewHolder -> viewHolder.bind(itemViewType(position) as GithubProfileInformation)
        }
    }

    internal fun updateDataSource(newDataSource: List<ViewType>) {
        dataSource = newDataSource
    }

    private fun itemViewType(position: Int) = dataSource[position]

    internal fun setOnItemClicked(onItemClicked: OnItemClicked) {
        itemClicked = onItemClicked
    }
}