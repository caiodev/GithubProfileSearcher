package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.viewTypes.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.viewHolders.GithubProfileInformationViewHolder

class GithubProfileAdapter(private val snackBar: Snackbar) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var dataSource = listOf<GithubProfileInformation>()

    override fun getItemCount() = dataSource.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return GithubProfileInformationViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.github_profile_view_holder_layout,
                parent,
                false
            )
        , snackBar)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        (viewHolder as GithubProfileInformationViewHolder).bind(dataSource[position])
    }

    internal fun updateDataSource(newDataSource: List<GithubProfileInformation>) {
        dataSource = newDataSource
    }
}