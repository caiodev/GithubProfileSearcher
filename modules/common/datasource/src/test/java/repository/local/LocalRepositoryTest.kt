package repository.local

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.base.repository.local.LocalRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.testing.TestSteps
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.testing.coroutines.junit5.CoroutinesTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import repository.local.fakes.database.FakeDatabase
import repository.local.fakes.protoDataStore.manager.FakeKeyValueStorageManager

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
                    localRepository.obtainProtoDataStore().obtainData().pageNumber,
                )
                assertEquals(
                    false,
                    localRepository.obtainProtoDataStore().obtainData().isHeaderVisible,
                )
                assertEquals(
                    "",
                    localRepository.obtainProtoDataStore().obtainData().currentProfile,
                )
            }
        }
    }

    @Test
    fun updateDataStoreValue_valuesOfVariousTypes_returnChangedValues() {
        doWhen {
            localRepository.obtainProtoDataStore().apply {
                runBlockingTest {
                    updateData(
                        githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.dataStore.serializer.model.ProfilePreferences(
                            pageNumber = 7,
                        ),
                    )
                    updateData(obtainData().copy(isHeaderVisible = true))
                    updateData(obtainData().copy(currentProfile = "torvalds"))
                }
            }
        }

        then {
            runBlockingTest {
                assertEquals(
                    7,
                    localRepository.obtainProtoDataStore().obtainData().pageNumber,
                )
                assertEquals(
                    true,
                    localRepository.obtainProtoDataStore().obtainData().isHeaderVisible,
                )
                assertEquals(
                    "torvalds",
                    localRepository.obtainProtoDataStore().obtainData().currentProfile,
                )
            }
        }
    }

    @Test
    fun saveValueToDataStore_commandToClearAllValuesInsideProtoDataStore_clearAllValuesInsideProtoDataStore() {
        given {
            runBlockingTest {
                localRepository.obtainProtoDataStore().updateData(
                    githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.dataStore.serializer.model.ProfilePreferences(
                        pageNumber = 7,
                    ),
                )
            }
        }

        doWhen {
            runBlockingTest {
                localRepository.obtainProtoDataStore().updateData(githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.dataStore.serializer.model.ProfilePreferences())
            }
        }

        then {
            runBlockingTest {
                assertEquals(0, localRepository.obtainProtoDataStore().obtainData().pageNumber)
            }
        }
    }
}
