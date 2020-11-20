package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.viewHolder

import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.databinding.GithubProfileViewHolderLayoutBinding
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.imageLoading.ImageLoading.loadImage
import okhttp3.internal.format

class GithubProfileInformationViewHolder(
    private val itemBinding: GithubProfileViewHolderLayoutBinding,
    onItemSelected: OnItemSelectedListener
) :
    RecyclerView.ViewHolder(itemBinding.root) {

    private var githubProfileUrl = ""

    init {
        itemBinding.parentLayout.setOnClickListener {
            onItemSelected.onItemSelected(githubProfileUrl)
        }
    }

    fun bind(model: GithubProfileInformation) {
        githubProfileUrl = model.profileUrl

        model.userId.let {
            itemBinding.userId.text =
                format(itemView.context.getString(R.string.user_id), it.toString())
        }

        model.login.let {
            itemBinding.userLogin.text =
                format(itemView.context.getString(R.string.login), it)
        }

        loadImage(
            itemView.context,
            model.userImage,
            R.mipmap.ic_launcher,
            itemBinding.userAvatar
        )
    }
}
