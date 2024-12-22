package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.feature.profile.viewHolder

import androidx.recyclerview.widget.RecyclerView
import coil3.load
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.model.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.databinding.ProfileViewHolderLayoutBinding
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.utils.types.string.emptyString
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.resources.R as Resources

class ProfileInformationViewHolder(
    private val itemBinding: ProfileViewHolderLayoutBinding,
    onItemSelected: OnItemSelectedListener,
) : RecyclerView.ViewHolder(itemBinding.root) {
    private var profileUrl = emptyString()

    init {
        itemBinding.parentLayout.setOnClickListener {
            onItemSelected.onItemSelected(profileUrl)
        }
    }

    fun bind(model: UserProfile) {
        profileUrl = model.profileUrl

        model.profileId.let {
            itemBinding.profileId.text =
                String.format(itemView.context.getString(Resources.string.uid), it.toString())
        }

        model.login.let {
            itemBinding.login.text =
                String.format(itemView.context.getString(Resources.string.login), it)
        }

        itemBinding.profileAvatar.load(model.profileImage)
    }
}
