package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class UserProfile(
    @ColumnInfo
    @SerialName("login")
    val login: String = "",
    @ColumnInfo
    @SerialName("html_url")
    val profileUrl: String = "",
    @PrimaryKey
    @SerialName("id")
    val userId: Long = 0,
    @ColumnInfo
    @SerialName("avatar_url")
    val userImage: String = "",
)
