package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.viewHolders

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.viewTypes.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.showUserRepositoryInformation.view.RepositoryInfoActivity
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.showErrorSnackBar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.imageLoading.ImageLoader.loadImage
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.network.NetworkChecking
import kotlinx.android.synthetic.main.github_profile_view_holder_layout.view.*
import okhttp3.internal.format

class GithubProfileInformationViewHolder(
    itemView: View, snackBar: Snackbar
) :
    RecyclerView.ViewHolder(itemView) {

    private var githubProfileUrl = ""

    init {
        itemView.parentLayout.setOnClickListener {
            itemView.apply {
                NetworkChecking.checkIfInternetConnectionIsAvailable(
                    this.context,
                    onConnectionAvailable = {
                        this.context.startActivity(
                            Intent(
                                this.context,
                                RepositoryInfoActivity::class.java
                            ).putExtra(Constants.githubProfileUrl, githubProfileUrl)
                        )
                    },
                    onConnectionUnavailable = { this.context.showErrorSnackBar(snackBar, R.string.no_connection_error) })
            }
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