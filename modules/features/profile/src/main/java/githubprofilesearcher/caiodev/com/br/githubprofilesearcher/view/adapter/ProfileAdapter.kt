package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.cast.ValueCasting.castTo
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.profile.databinding.ProfileViewHolderLayoutBinding
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.viewHolder.OnItemSelectedListener
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.viewHolder.ProfileInformationViewHolder

class ProfileAdapter(private val onItemSelectedListener: OnItemSelectedListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var dataSource = listOf<UserProfile>()

    override fun getItemCount() = dataSource.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        return ProfileInformationViewHolder(
            ProfileViewHolderLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
            onItemSelectedListener,
        )
    }

    override fun onBindViewHolder(
        viewHolder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        castTo<ProfileInformationViewHolder>(viewHolder)?.bind(dataSource[position])
    }

    internal fun updateDataSource(newDataSource: List<UserProfile>) {
        dataSource = newDataSource
    }
}
