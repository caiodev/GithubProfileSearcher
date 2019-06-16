package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.viewTypes.GithubUserInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.OnItemClicked
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.imageLoading.LoadImage
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.text.TextFormatting.concatenateStrings
import kotlinx.android.synthetic.main.github_profile_view_holder.view.*
import timber.log.Timber

class GithubUserInformationViewHolder(itemView: View, private val onItemClicked: OnItemClicked?) :
    RecyclerView.ViewHolder(itemView) {

    init {
        itemView.parentLayout.setOnClickListener {
            onItemClicked?.onItemClick(adapterPosition, Constants.githubProfileRecyclerViewCell)
        }
    }

    fun bind(model: GithubUserInformation) {

        model.userId.let {
            itemView.userId.text =
                concatenateStrings(itemView.context.getString(R.string.user_id), it.toString())
        }

        model.login.let {
            itemView.userLogin.text =
                concatenateStrings(itemView.context.getString(R.string.login), it)
        }

        model.score?.let {
            itemView.userScore.text = concatenateStrings(
                itemView.context.getString(R.string.score),
                it.toString()
            )
        }

        LoadImage.loadImage(
            itemView.context, model.userImage,
            R.mipmap.ic_launcher,
            itemView.userAvatar
        )

        Timber.i("VALUE: %s", "Insert: ${model.profileUrl}")
    }
}