package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.unit.utils.base.repository.local

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.unit.utils.base.repository.local.fakes.database.FakeDatabase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.unit.utils.base.repository.local.fakes.protoDataStore.manager.FakeProtoDataStoreManager
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.LocalRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.datastore.model.UserPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import utils.base.TestSteps
import utils.base.coroutines.CoroutinesTestExtension

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
class LocalRepositoryTest : TestSteps {

    private lateinit var fakeProtoDataStore: FakeProtoDataStoreManager
    private lateinit var fakeDatabase: FakeDatabase
    private lateinit var localRepository: LocalRepository

    @BeforeEach
    override fun setupDependencies() {
        fakeProtoDataStore = FakeProtoDataStoreManager()
        fakeDatabase = FakeDatabase()
        localRepository = LocalRepository(fakeProtoDataStore, fakeDatabase)
    }

    @Test
    fun obtainValueFromDataStore_valuesOfVariousTypes_returnDefaultValues() {
        then {
            runBlockingTest {
                assertEquals(
                    0,
                    localRepository.obtainProtoDataStore().obtainDataStoreValue().pageNumber
                )
                assertEquals(
                    false,
                    localRepository.obtainProtoDataStore().obtainDataStoreValue().isHeaderVisible
                )
                assertEquals(
                    "",
                    localRepository.obtainProtoDataStore().obtainDataStoreValue().currentProfile
                )
            }
        }
    }

    @Test
    fun updateDataStoreValue_valuesOfVariousTypes_returnChangedValues() {
        doWhen {
            localRepository.obtainProtoDataStore().apply {
                runBlockingTest {
                    updateDataStoreValue(UserPreferences(pageNumber = 7))
                    updateDataStoreValue(obtainDataStoreValue().copy(isHeaderVisible = true))
                    updateDataStoreValue(obtainDataStoreValue().copy(currentProfile = "torvalds"))
                }
            }
        }

        then {
            runBlockingTest {
                assertEquals(
                    7,
                    localRepository.obtainProtoDataStore().obtainDataStoreValue().pageNumber
                )
                assertEquals(
                    true,
                    localRepository.obtainProtoDataStore().obtainDataStoreValue().isHeaderVisible
                )
                assertEquals(
                    "torvalds",
                    localRepository.obtainProtoDataStore().obtainDataStoreValue().currentProfile
                )
            }
        }
    }

    @Test
    fun saveValueToDataStore_commandToClearAllValuesInsideProtoDataStore_clearAllValuesInsideProtoDataStore() {
        given {
            runBlockingTest {
                localRepository.obtainProtoDataStore().updateDataStoreValue(UserPreferences(pageNumber = 7))
            }
        }

        doWhen {
            runBlockingTest {
                localRepository.obtainProtoDataStore().updateDataStoreValue(UserPreferences())
            }
        }

        then {
            runBlockingTest {
                assertEquals(0, localRepository.obtainProtoDataStore().obtainDataStoreValue().pageNumber)
            }
        }
    }
}
