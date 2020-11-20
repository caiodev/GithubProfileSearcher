package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.datastore.serializer

import androidx.datastore.core.Serializer
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.datastore.model.UserPreferences
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.io.OutputStream

object UserPreferencesSerializer : Serializer<UserPreferences> {

    override fun readFrom(input: InputStream): UserPreferences {
        return try {
            with(DataInputStream(input)) {
                UserPreferences(
                    readBoolean(),
                    readUTF(),
                    readBoolean(),
                    readUTF(),
                    readUTF(),
                    readInt(),
                    readBoolean(),
                    readBoolean(),
                    readBoolean(),
                    readBoolean(),
                    readBoolean(),
                    readBoolean(),
                    readInt(),
                    readBoolean(),
                    readBoolean(),
                    readBoolean(),
                    readBoolean()
                )
            }
        } catch (exception: Exception) {
            UserPreferences()
        }
    }

    override fun writeTo(
        t: UserPreferences,
        output: OutputStream
    ) {
        with(DataOutputStream(output)) {
            writeBoolean(t.hasUserDeletedProfileText)
            writeUTF(t.textInputEditTextProfile)
            writeBoolean(t.shouldRecyclerViewAnimationBeExecuted)
            writeUTF(t.temporaryCurrentProfile)
            writeUTF(t.currentProfile)
            writeInt(t.pageNumber)
            writeBoolean(t.hasASuccessfulCallAlreadyBeenMade)
            writeBoolean(t.hasLastCallBeenUnsuccessful)
            writeBoolean(t.isThereAnOngoingCall)
            writeBoolean(t.hasUserRequestedUpdatedData)
            writeBoolean(t.shouldASearchBePerformed)
            writeBoolean(t.isTextInputEditTextEmpty)
            writeInt(t.numberOfItems)
            writeBoolean(t.isHeaderVisible)
            writeBoolean(t.isEndOfResultsViewVisible)
            writeBoolean(t.isPaginationLoadingViewVisible)
            writeBoolean(t.isRetryViewVisible)
        }
    }

    override val defaultValue = UserPreferences()
}
