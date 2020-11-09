package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.imageLoading.ImageLoading.loadImage
import kotlinx.android.synthetic.main.github_profile_view_holder_layout.view.parentLayout
import kotlinx.android.synthetic.main.github_profile_view_holder_layout.view.userAvatar
import kotlinx.android.synthetic.main.github_profile_view_holder_layout.view.userId
import kotlinx.android.synthetic.main.github_profile_view_holder_layout.view.userLogin
import okhttp3.internal.format

class GithubProfileInformationViewHolder(
    itemView: View,
    onItemSelected: OnItemSelectedListener
) :
    RecyclerView.ViewHolder(itemView) {

    private var githubProfileUrl = ""

    init {
        itemView.parentLayout.setOnClickListener {
            onItemSelected.onItemSelected(githubProfileUrl)
        }
    }

    fun bind(model: GithubProfileInformation) {
        githubProfileUrl = model.profileUrl

        model.userId.let {
            itemView.userId.text =
                format(itemView.context.getString(R.string.user_id), it.toString())
        }

        model.login.let {
            itemView.userLogin.text =
                format(itemView.context.getString(R.string.login), it)
        }

        loadImage(
            itemView.context,
            model.userImage,
            R.mipmap.ic_launcher,
            itemView.userAvatar
        )
    }
}
