package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.databinding.GithubProfileViewHolderLayoutBinding
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.viewHolder.GithubProfileInformationViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.viewHolder.OnItemSelectedListener
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.cast.ValueCasting.castValue

class GithubProfileAdapter(private val onItemSelectedListener: OnItemSelectedListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var dataSource = listOf<GithubProfileInformation>()

    override fun getItemCount() = dataSource.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return GithubProfileInformationViewHolder(
            GithubProfileViewHolderLayoutBinding.inflate(
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

    internal fun updateDataSource(newDataSource: List<GithubProfileInformation>) {
        dataSource = newDataSource
    }
}
