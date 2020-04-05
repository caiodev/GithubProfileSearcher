package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.viewTypes.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.githubProfileCell
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.imageLoading.ImageLoader.loadImage
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.interfaces.OnItemClicked
import kotlinx.android.synthetic.main.github_profile_view_holder_layout.view.*
import okhttp3.internal.format

class GithubProfileInformationViewHolder(
    itemView: View,
    private val onItemClicked: OnItemClicked?
) :
    RecyclerView.ViewHolder(itemView) {

    init {
        itemView.parentLayout.setOnClickListener {
            onItemClicked?.onItemClick(adapterPosition, githubProfileCell)
        }
    }

    fun bind(model: GithubProfileInformation) {

        model.userId.let {
            itemView.userId.text =
                format(itemView.context.getString(R.string.user_id), it.toString())
        }

        model.login.let {
            itemView.userLogin.text =
                format(itemView.context.getString(R.string.login), it)
        }

        loadImage(
            model.userImage,
            R.mipmap.ic_launcher,
            itemView.userAvatar
        )
    }
}