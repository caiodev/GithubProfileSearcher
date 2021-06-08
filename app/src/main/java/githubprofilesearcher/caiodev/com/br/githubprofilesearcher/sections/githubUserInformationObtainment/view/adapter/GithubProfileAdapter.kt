package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.databinding.ProfileViewHolderLayoutBinding
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.UserProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.viewHolder.GithubProfileInformationViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.viewHolder.OnItemSelectedListener
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.cast.ValueCasting.castValue

class GithubProfileAdapter(private val onItemSelectedListener: OnItemSelectedListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var dataSource = listOf<UserProfileInformation>()

    override fun getItemCount() = dataSource.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return GithubProfileInformationViewHolder(
            ProfileViewHolderLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onItemSelectedListener
        )
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        castValue<GithubProfileInformationViewHolder>(viewHolder).bind(dataSource[position])
    }

    internal fun updateDataSource(newDataSource: List<UserProfileInformation>) {
        dataSource = newDataSource
    }
}
