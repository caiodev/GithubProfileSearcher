package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.unit.utils.base.repository.local

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.repository.local.dataStore.model.ProfilePreferences
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.repository.local.dataStore.serializer.model.ProfilePreferences
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.unit.utils.base.repository.local.fakes.database.FakeDatabase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.unit.utils.base.repository.local.fakes.protoDataStore.manager.FakeKeyValueStorageManager
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.LocalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import utils.base.TestSteps
import utils.base.coroutines.junit5.CoroutinesTestExtension

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
class LocalRepositoryTest : TestSteps {

    private lateinit var fakeProtoDataStore: FakeKeyValueStorageManager
    private lateinit var fakeDatabase: FakeDatabase
    private lateinit var localRepository: LocalRepository

    @BeforeEach
    override fun setupDependencies() {
        fakeProtoDataStore = FakeKeyValueStorageManager()
        fakeDatabase = FakeDatabase()
        localRepository = LocalRepository(fakeProtoDataStore, fakeDatabase)
    }

    @Test
    fun obtainValueFromDataStore_valuesOfVariousTypes_returnDefaultValues() {
        then {
            runBlockingTest {
                assertEquals(
                    0,
                    localRepository.obtainProtoDataStore().obtainData().pageNumber
                )
                assertEquals(
                    false,
                    localRepository.obtainProtoDataStore().obtainData().isHeaderVisible
                )
                assertEquals(
                    "",
                    localRepository.obtainProtoDataStore().obtainData().currentProfile
                )
            }
        }
    }

    @Test
    fun updateDataStoreValue_valuesOfVariousTypes_returnChangedValues() {
        doWhen {
            localRepository.obtainProtoDataStore().apply {
                runBlockingTest {
                    updateData(ProfilePreferences(pageNumber = 7))
                    updateData(obtainData().copy(isHeaderVisible = true))
                    updateData(obtainData().copy(currentProfile = "torvalds"))
                }
            }
        }

        then {
            runBlockingTest {
                assertEquals(
                    7,
                    localRepository.obtainProtoDataStore().obtainData().pageNumber
                )
                assertEquals(
                    true,
                    localRepository.obtainProtoDataStore().obtainData().isHeaderVisible
                )
                assertEquals(
                    "torvalds",
                    localRepository.obtainProtoDataStore().obtainData().currentProfile
                )
            }
        }
    }

    @Test
    fun saveValueToDataStore_commandToClearAllValuesInsideProtoDataStore_clearAllValuesInsideProtoDataStore() {
        given {
            runBlockingTest {
                localRepository.obtainProtoDataStore().updateData(ProfilePreferences(pageNumber = 7))
            }
        }

        doWhen {
            runBlockingTest {
                localRepository.obtainProtoDataStore().updateData(ProfilePreferences())
            }
        }

        then {
            runBlockingTest {
                assertEquals(0, localRepository.obtainProtoDataStore().obtainData().pageNumber)
            }
        }
    }
}
