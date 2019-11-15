package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.viewTypes.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.viewTypes.Header
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.view.GithubProfileInformationViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.view.HeaderViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.interfaces.OnItemClicked
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.interfaces.viewTypes.RecyclerViewViewTypes
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.interfaces.viewTypes.ViewType
import timber.log.Timber

class GithubProfileAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var itemClicked: OnItemClicked? = null
    private var dataSource: List<ViewType>? = null

    override fun getItemCount() = getTotalCount()

    override fun getItemViewType(position: Int) = itemViewType(position).provideViewType()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val itemView = LayoutInflater.from(parent.context)

        return when (viewType) {

            RecyclerViewViewTypes.header -> {
                HeaderViewHolder(
                    itemView.inflate(
                        R.layout.header_view_holder,
                        parent,
                        false
                    )
                )
            }

            else -> {
                GithubProfileInformationViewHolder(
                    itemView.inflate(
                        R.layout.github_profile_view_holder,
                        parent,
                        false
                    )
                    , itemClicked
                )
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is HeaderViewHolder -> viewHolder.bind(itemViewType(position) as Header)
            is GithubProfileInformationViewHolder -> {
                Timber.i("Login: %s", (itemViewType(position) as GithubProfileInformation).login)
                viewHolder.bind(itemViewType(position) as GithubProfileInformation)
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

    fun setOnItemClicked(onItemClicked: OnItemClicked) {
        itemClicked = onItemClicked
    }
}