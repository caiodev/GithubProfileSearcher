package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.fakes.database.FakeDatabase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.fakes.sharedPreferences.FakeSharedPreferences
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.base.TestSteps

class LocalRepositoryTest : TestSteps {

    private lateinit var fakeSharedPreferences: FakeSharedPreferences
    private lateinit var fakeDatabase: FakeDatabase
    private lateinit var localRepository: LocalRepository

    @BeforeEach
    override fun setupDependencies() {
        fakeSharedPreferences = FakeSharedPreferences()
        fakeDatabase = FakeDatabase()
        localRepository = LocalRepository(fakeSharedPreferences, fakeDatabase)
    }

    @Test
    fun retrieveValueFromSharedPreferences_valuesOfVariousTypes_returnExpectedValues() {

        var boolean = true
        var integer = -1
        var string = "dummy"

        doWhen {
            boolean = localRepository.retrieveValueFromSharedPreferences("boolean", boolean)
            integer = localRepository.retrieveValueFromSharedPreferences("integer", integer)
            string = localRepository.retrieveValueFromSharedPreferences("string", string)
        }

        then {
            assertEquals(false, boolean)
            assertEquals(0, integer)
            assertEquals("", string)
        }
    }

    @Test
    fun retrieveValueFromSharedPreferences_valuesOfVariousTypes_returnUnexpectedValues() {

        var long = 20L

        doWhen {
            long = localRepository.retrieveValueFromSharedPreferences("long", 0L)
        }

        then {
            assertEquals(0L, long)
        }
    }

    @Test
    fun saveValueToSharedPreferences_valuesOfVariousTypes_saveAllOfThemLocally() {

        doWhen {
            localRepository.saveValueToSharedPreferences("boolean", true)
            localRepository.saveValueToSharedPreferences("integer", 42)
            localRepository.saveValueToSharedPreferences("string", "dummy")
        }

        then {
            assertEquals(true, fakeSharedPreferences.fakeEditor.fakeSharedPreferencesModel.boolean)
            assertEquals(42, fakeSharedPreferences.fakeEditor.fakeSharedPreferencesModel.integer)
            assertEquals(
                "dummy",
                fakeSharedPreferences.fakeEditor.fakeSharedPreferencesModel.string
            )
        }
    }

    @Test
    fun clear_commandToClearAllValuesInsideTheObjectUsed_clearAllValuesInsideTheObjectUsed() {

        given {
            localRepository.saveValueToSharedPreferences("boolean", true)
            localRepository.saveValueToSharedPreferences("integer", 42)
            localRepository.saveValueToSharedPreferences("string", "dummy")
        }

        doWhen {
            localRepository.clearSharedPreferences()
        }

        then {
            assertEquals(false, fakeSharedPreferences.fakeEditor.fakeSharedPreferencesModel.boolean)
            assertEquals(0, fakeSharedPreferences.fakeEditor.fakeSharedPreferencesModel.integer)
            assertEquals(
                "",
                fakeSharedPreferences.fakeEditor.fakeSharedPreferencesModel.string
            )
        }
    }
}