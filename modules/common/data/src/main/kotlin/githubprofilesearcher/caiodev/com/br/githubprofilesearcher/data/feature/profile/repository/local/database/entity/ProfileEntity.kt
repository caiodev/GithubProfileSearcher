package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.feature.profile.repository.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.utils.types.number.defaultLong
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.utils.types.string.emptyString

@Entity(tableName = "Profile")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true) val orderingId: Long = defaultLong(),
    @ColumnInfo val profileId: Long = defaultLong(),
    @ColumnInfo val login: String = emptyString(),
    @ColumnInfo val profileUrl: String = emptyString(),
    @ColumnInfo val profileImage: String = emptyString(),
)
