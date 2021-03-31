package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.unit.utils.base.repository.local

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.unit.utils.base.repository.local.fakes.database.FakeDatabase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.unit.utils.base.repository.local.fakes.protoDataStore.manager.FakeProtoDataStoreManager
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.LocalRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository.local.dataStore.model.ProfilePreferences
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
                    localRepository.obtainProtoDataStore().obtainValue().pageNumber
                )
                assertEquals(
                    false,
                    localRepository.obtainProtoDataStore().obtainValue().isHeaderVisible
                )
                assertEquals(
                    "",
                    localRepository.obtainProtoDataStore().obtainValue().currentProfile
                )
            }
        }
    }

    @Test
    fun updateDataStoreValue_valuesOfVariousTypes_returnChangedValues() {
        doWhen {
            localRepository.obtainProtoDataStore().apply {
                runBlockingTest {
                    updateValue(ProfilePreferences(pageNumber = 7))
                    updateValue(obtainValue().copy(isHeaderVisible = true))
                    updateValue(obtainValue().copy(currentProfile = "torvalds"))
                }
            }
        }

        then {
            runBlockingTest {
                assertEquals(
                    7,
                    localRepository.obtainProtoDataStore().obtainValue().pageNumber
                )
                assertEquals(
                    true,
                    localRepository.obtainProtoDataStore().obtainValue().isHeaderVisible
                )
                assertEquals(
                    "torvalds",
                    localRepository.obtainProtoDataStore().obtainValue().currentProfile
                )
            }
        }
    }

    @Test
    fun saveValueToDataStore_commandToClearAllValuesInsideProtoDataStore_clearAllValuesInsideProtoDataStore() {
        given {
            runBlockingTest {
                localRepository.obtainProtoDataStore().updateValue(ProfilePreferences(pageNumber = 7))
            }
        }

        doWhen {
            runBlockingTest {
                localRepository.obtainProtoDataStore().updateValue(ProfilePreferences())
            }
        }

        then {
            runBlockingTest {
                assertEquals(0, localRepository.obtainProtoDataStore().obtainValue().pageNumber)
            }
        }
    }
}
