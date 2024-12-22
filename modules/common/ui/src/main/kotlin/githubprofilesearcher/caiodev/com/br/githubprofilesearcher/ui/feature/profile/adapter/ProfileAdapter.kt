package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.feature.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.model.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.databinding.ProfileViewHolderLayoutBinding
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.feature.profile.viewHolder.OnItemSelectedListener
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.feature.profile.viewHolder.ProfileInformationViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.utils.cast.castTo

class ProfileAdapter(
    private val onItemSelectedListener: OnItemSelectedListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var dataSource = listOf<UserProfile>()

    override fun getItemCount() = dataSource.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder =
        ProfileInformationViewHolder(
            ProfileViewHolderLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
            onItemSelectedListener,
        )

    override fun onBindViewHolder(
        viewHolder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        viewHolder.castTo<ProfileInformationViewHolder>()?.bind(dataSource[position])
    }

    fun updateDataSource(newDataSource: List<UserProfile>) {
        dataSource = newDataSource
    }
}
