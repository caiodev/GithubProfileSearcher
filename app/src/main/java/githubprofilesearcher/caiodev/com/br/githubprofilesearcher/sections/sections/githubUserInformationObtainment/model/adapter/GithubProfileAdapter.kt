package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.viewTypes.Generic
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.viewTypes.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.viewTypes.Header
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.view.GenericViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.view.GithubProfileInformationViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.view.HeaderViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.githubProfileCell
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.header
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.interfaces.OnItemClicked
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.interfaces.viewTypes.ViewType

class GithubProfileAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var itemClicked: OnItemClicked? = null
    private var dataSource: List<ViewType>? = null
    private var genericViewHolder: GenericViewHolder? = null

    override fun getItemCount() = getTotalCount()

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

            else -> GenericViewHolder(
                itemView.inflate(
                    R.layout.generic_view_holder_layout,
                    parent,
                    false
                ), itemClicked
            )
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is HeaderViewHolder -> viewHolder.bind(itemViewType(position) as Header)
            is GithubProfileInformationViewHolder -> viewHolder.bind(itemViewType(position) as GithubProfileInformation)
            is GenericViewHolder -> {
                viewHolder.bind(itemViewType(position) as Generic)
                genericViewHolder = viewHolder
            }
        }
    }

    internal fun updateDataSource(newDataSource: List<ViewType>) {
        dataSource = newDataSource
    }

    private fun getTotalCount(): Int {
        dataSource?.size?.let {
            return it
        }
        return 0
    }

    private fun itemViewType(position: Int): ViewType {
        dataSource?.get(position)?.let {
            return it
        }
        return Header(0)
    }

    internal fun setOnItemClicked(onItemClicked: OnItemClicked) {
        itemClicked = onItemClicked
    }

    internal fun changeGenericState(state: Int) {
        genericViewHolder?.changeState(state)
    }
}