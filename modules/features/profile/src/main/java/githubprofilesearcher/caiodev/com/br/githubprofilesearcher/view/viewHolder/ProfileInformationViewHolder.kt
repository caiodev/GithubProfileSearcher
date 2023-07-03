package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.viewHolder

import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import coil.load
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.profile.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.profile.databinding.ProfileViewHolderLayoutBinding
import okhttp3.internal.format

class ProfileInformationViewHolder(
    private val itemBinding: ProfileViewHolderLayoutBinding,
    onItemSelected: OnItemSelectedListener,
) :
    RecyclerView.ViewHolder(itemBinding.root) {

    private var profileUrl = ""

    init {
        itemBinding.parentLayout.setOnClickListener {
            onItemSelected.onItemSelected(profileUrl)
        }
    }

    fun bind(model: UserProfile) {
        profileUrl = model.profileUrl

        model.userId.let {
            itemBinding.userId.text =
                format(itemView.context.getString(R.string.uid), it.toString())
        }

        model.login.let {
            itemBinding.userLogin.text =
                format(itemView.context.getString(R.string.login), it)
        }

        itemBinding.userAvatar.load(model.userImage, itemView.context.imageLoader)
    }
}
