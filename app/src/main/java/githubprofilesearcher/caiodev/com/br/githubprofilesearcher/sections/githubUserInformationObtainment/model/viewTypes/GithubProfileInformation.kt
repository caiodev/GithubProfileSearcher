package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.viewTypes

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class GithubProfileInformation(
    val login: String = "",
    @SerialName("html_url") val profileUrl: String = "",
    @SerialName("id") val userId: Long = 0,
    @SerialName("avatar_url") val userImage: String = ""
) : Parcelable