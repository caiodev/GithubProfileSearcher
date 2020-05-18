package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.viewTypes.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.viewHolders.GithubProfileInformationViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.interfaces.OnItemClicked

class GithubProfileAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var itemClicked: OnItemClicked
    private var dataSource = listOf<GithubProfileInformation>()

    override fun getItemCount() = dataSource.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return GithubProfileInformationViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.github_profile_view_holder_layout,
                parent,
                false
            ), itemClicked
        )
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        (viewHolder as GithubProfileInformationViewHolder).bind(dataSource[position])
    }

    internal fun updateDataSource(newDataSource: List<GithubProfileInformation>) {
        dataSource = newDataSource
    }

    internal fun setOnItemClicked(onItemClicked: OnItemClicked) {
        itemClicked = onItemClicked
    }
}