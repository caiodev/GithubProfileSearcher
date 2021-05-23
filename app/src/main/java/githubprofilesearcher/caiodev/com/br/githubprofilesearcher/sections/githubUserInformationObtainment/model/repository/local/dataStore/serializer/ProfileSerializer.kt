package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository.local.dataStore.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ProfilePreferences
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object ProfileSerializer : Serializer<ProfilePreferences> {

    const val profileProtoFileName = "profile.proto"

    override val defaultValue: ProfilePreferences = ProfilePreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): ProfilePreferences {
        try {
            return ProfilePreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: ProfilePreferences, output: OutputStream) = t.writeTo(output)
}
