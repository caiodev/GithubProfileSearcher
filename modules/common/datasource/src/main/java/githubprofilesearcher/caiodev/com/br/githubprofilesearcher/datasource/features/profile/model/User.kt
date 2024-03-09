package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.types.string.emptyString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class User(
    @ColumnInfo
    @SerialName("login")
    val login: String = emptyString(),
    @ColumnInfo
    @SerialName("html_url")
    val profileUrl: String = emptyString(),
    @PrimaryKey
    @SerialName("id")
    val userId: Long = 0,
    @ColumnInfo
    @SerialName("avatar_url")
    val userImage: String = emptyString(),
)
