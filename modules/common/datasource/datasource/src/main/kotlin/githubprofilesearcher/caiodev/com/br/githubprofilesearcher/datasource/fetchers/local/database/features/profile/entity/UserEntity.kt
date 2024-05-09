package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.database.features.profile.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.types.number.defaultLong
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.types.string.emptyString

@Entity("User")
data class UserEntity(
    @ColumnInfo val login: String = emptyString(),
    @ColumnInfo val profileUrl: String = emptyString(),
    @PrimaryKey val userId: Long = defaultLong(),
    @ColumnInfo val userImage: String = emptyString(),
)
