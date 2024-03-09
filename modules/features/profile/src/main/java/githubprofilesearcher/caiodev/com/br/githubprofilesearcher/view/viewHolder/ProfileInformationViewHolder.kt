package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.viewHolder

import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import coil.load
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.types.string.emptyString
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.profile.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.profile.databinding.ProfileViewHolderLayoutBinding

class ProfileInformationViewHolder(
    private val itemBinding: ProfileViewHolderLayoutBinding,
    onItemSelected: OnItemSelectedListener,
) :
    RecyclerView.ViewHolder(itemBinding.root) {
    private var profileUrl = emptyString()

    init {
        itemBinding.parentLayout.setOnClickListener {
            onItemSelected.onItemSelected(profileUrl)
        }
    }

    fun bind(model: UserProfile) {
        profileUrl = model.profileUrl

        model.userId.let {
            itemBinding.userId.text =
                String.format(itemView.context.getString(R.string.uid), it.toString())
        }

        model.login.let {
            itemBinding.userLogin.text =
                String.format(itemView.context.getString(R.string.login), it)
        }

        itemBinding.userAvatar.load(model.userImage, itemView.context.imageLoader)
    }
}
