package com.example

import com.example.data.CaseSeeds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CaseSeedsTest {
    private val cases = CaseSeeds.cases
    private val case4 = cases.single { it.id == 4 }

    @Test
    fun case4UsesIntendedSolutionValues() {
        assertEquals("Rosalie Crane", case4.solutionSuspect)
        assertEquals("Poison Dart", case4.solutionWeapon)
        assertEquals("Greenhouse", case4.solutionLocation)
        assertEquals("Dr. Mira Elgin", case4.solutionLiar)
    }

    @Test
    fun case4IncludesGarrettInVaultClue() {
        assertTrue(case4.clues.contains("Garrett Thorne was confirmed to be inside the Antique Vault."))
    }

    @Test
    fun case4HasExactlyOneLie() {
        assertEquals(1, case4.statements.count { it.isLie })
    }

    @Test
    fun allSolutionValuesExistInCaseLists() {
        cases.forEach { case ->
            assertTrue(case.suspects.contains(case.solutionSuspect))
            assertTrue(case.weapons.contains(case.solutionWeapon))
            assertTrue(case.locations.contains(case.solutionLocation))
            case.solutionLiar?.let { liar -> assertTrue(case.suspects.contains(liar)) }
            if (case.hasLiar) assertNotNull(case.solutionLiar)
        }
    }

    @Test
    fun everyCaseHasThreeSuspectsWeaponsAndLocations() {
        cases.forEach { case ->
            assertEquals(3, case.suspects.size)
            assertEquals(3, case.weapons.size)
            assertEquals(3, case.locations.size)
        }
    }

    @Test
    fun allCaseIdsAreUnique() {
        assertEquals(cases.size, cases.map { it.id }.toSet().size)
    }

    @Test
    fun difficultiesUseAllowedValues() {
        val allowed = setOf("Easy", "Medium", "Hard")
        cases.forEach { case -> assertTrue(case.difficulty in allowed) }
    }

    @Test
    fun case4HasOneValidAssignmentUnderExplicitLogic() {
        val validAssignments = permutations(case4.weapons).flatMap { weaponsBySuspect ->
            permutations(case4.locations).map { locationsBySuspect ->
                case4.suspects.associateWith { suspect ->
                    Assignment(
                        weapon = weaponsBySuspect[case4.suspects.indexOf(suspect)],
                        location = locationsBySuspect[case4.suspects.indexOf(suspect)]
                    )
                }
            }
        }.filter { assignment ->
            val garrett = assignment.getValue("Garrett Thorne")
            val mira = assignment.getValue("Dr. Mira Elgin")
            val rosalie = assignment.getValue("Rosalie Crane")

            val verifiedCluesHold = garrett.weapon == "Pocket Knife" &&
                garrett.location == "Antique Vault" &&
                mira.location == "Trophy Room"

            val statementTruths = listOf(
                garrett.weapon == "Pocket Knife",
                garrett.location == "Greenhouse",
                assignment.any { (_, details) ->
                    details.weapon == "Chloroform Rag" && details.location == "Trophy Room"
                }
            )

            verifiedCluesHold && statementTruths.count { !it } == 1 &&
                rosalie.weapon == "Poison Dart" && rosalie.location == "Greenhouse"
        }

        assertEquals(1, validAssignments.size)
        assertEquals(Assignment("Poison Dart", "Greenhouse"), validAssignments.single().getValue("Rosalie Crane"))
    }

    private data class Assignment(val weapon: String, val location: String)

    private fun <T> permutations(values: List<T>): List<List<T>> = when (values.size) {
        0 -> listOf(emptyList())
        else -> values.flatMap { value ->
            permutations(values - value).map { remainder -> listOf(value) + remainder }
        }
    }
}
