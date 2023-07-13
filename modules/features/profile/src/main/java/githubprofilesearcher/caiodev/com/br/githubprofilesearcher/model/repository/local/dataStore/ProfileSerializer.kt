package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.dataStore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import timber.log.Timber
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object ProfileSerializer : Serializer<ProfilePreferences> {

    const val profileProtoFileName = "profile.proto"
    override val defaultValue: ProfilePreferences = ProfilePreferences.getDefaultInstance()
    private const val TAG = "ProfileSerializer"

    override suspend fun readFrom(input: InputStream): ProfilePreferences {
        return try {
            val dis = DataInputStream(input)
            ProfilePreferences(
                hasUserDeletedProfileText = dis.readBoolean(),
                profile = dis.readUTF(),
                shouldRecyclerViewAnimationBeExecuted = dis.readBoolean(),
                temporaryCurrentProfile = dis.readUTF(),
                currentProfile = dis.readUTF(),
                pageNumber = dis.readInt(),
                hasASuccessfulCallAlreadyBeenMade = dis.readBoolean(),
                hasLastCallBeenUnsuccessful = dis.readBoolean(),
                isThereAnOngoingCall = dis.readBoolean(),
                hasUserRequestedUpdatedData = dis.readBoolean(),
                shouldASearchBePerformed = dis.readBoolean(),
                isTextInputEditTextNotEmpty = dis.readBoolean(),
                isHeaderVisible = dis.readBoolean(),
                isEndOfResultsViewVisible = dis.readBoolean(),
                isPaginationLoadingViewVisible = dis.readBoolean(),
                isRetryViewVisible = dis.readBoolean(),
                isLocalPopulation = dis.readBoolean(),
            )
        } catch (exception: CorruptionException) {
            Timber.e(TAG, exception)
            defaultValue
        }
    }

    override suspend fun writeTo(t: ProfilePreferences, output: OutputStream) {
        val dos = DataOutputStream(output)
        dos.writeBoolean(t.hasUserDeletedProfileText)
        dos.writeUTF(t.profile)
        dos.writeBoolean(t.shouldRecyclerViewAnimationBeExecuted)
        dos.writeUTF(t.temporaryCurrentProfile)
        dos.writeUTF(t.currentProfile)
        dos.writeInt(t.pageNumber)
        dos.writeBoolean(t.hasASuccessfulCallAlreadyBeenMade)
        dos.writeBoolean(t.hasLastCallBeenUnsuccessful)
        dos.writeBoolean(t.isThereAnOngoingCall)
        dos.writeBoolean(t.hasUserRequestedUpdatedData)
        dos.writeBoolean(t.shouldASearchBePerformed)
        dos.writeBoolean(t.isTextInputEditTextNotEmpty)
        dos.writeBoolean(t.isHeaderVisible)
        dos.writeBoolean(t.isEndOfResultsViewVisible)
        dos.writeBoolean(t.isPaginationLoadingViewVisible)
        dos.writeBoolean(t.isRetryViewVisible)
        dos.writeBoolean(t.isLocalPopulation)
    }
}
