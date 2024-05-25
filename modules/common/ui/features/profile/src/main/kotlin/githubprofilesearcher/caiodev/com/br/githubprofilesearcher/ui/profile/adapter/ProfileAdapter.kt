package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.cast.ValueCasting.castTo
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.profile.databinding.ProfileViewHolderLayoutBinding
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.profile.viewHolder.OnItemSelectedListener
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.profile.viewHolder.ProfileInformationViewHolder

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
