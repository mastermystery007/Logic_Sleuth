package com.example.data

data class Statement(
    val speaker: String,
    val text: String,
    val isLie: Boolean = false
)

data class Case(
    val id: Int,
    val title: String,
    val difficulty: String, // "Easy", "Medium", "Hard"
    val story: String,
    val suspects: List<String>,
    val weapons: List<String>,
    val locations: List<String>,
    val suspectDescriptions: Map<String, String>,
    val weaponDescriptions: Map<String, String>,
    val locationDescriptions: Map<String, String>,
    val clues: List<String>,
    val hasLiar: Boolean,
    val statements: List<Statement> = emptyList(),
    val solutionSuspect: String,
    val solutionWeapon: String,
    val solutionLocation: String,
    val solutionLiar: String? = null,
    val murderExplanation: String
)

object CaseSeeds {
    val cases = listOf(
        Case(
            id = 1,
            title = "The Silent Study",
            difficulty = "Easy",
            story = "Lord Sterling was found dead inside his private study at Sterling Manor. Investigators have identified three suspects, three possible weapons, and three relevant locations within the estate. Reconstruct each person’s movements and determine who killed Sterling, what weapon was used, and where the murder occurred.",
            suspects = listOf("Victor Hale", "Eleanor Voss", "Dr. Adrian Vale"),
            weapons = listOf("Antique Dagger", "Lead Pipe", "Poison Vial"),
            locations = listOf("Study", "Conservatory", "Library"),
            suspectDescriptions = mapOf(
                "Victor Hale" to "A polished investor whose bitter business dispute with Sterling had recently turned personal.",
                "Eleanor Voss" to "Sterling’s estranged sister and a beneficiary of his estate, outwardly composed despite years of family resentment.",
                "Dr. Adrian Vale" to "A chemist employed by Sterling’s pharmaceutical company, recently threatened with dismissal after a costly experiment failed."
            ),
            weaponDescriptions = mapOf(
                "Antique Dagger" to "An ornate medieval blade taken from the hallway display.",
                "Lead Pipe" to "A heavy pipe covered in plumbing grease.",
                "Poison Vial" to "A concentrated compound of deadly cyanide."
            ),
            locationDescriptions = mapOf(
                "Study" to "Lord Sterling’s private study, its desk disturbed in what may have been an attempt to stage a struggle.",
                "Conservatory" to "A warm glasshouse full of tropical ferns.",
                "Library" to "A quiet sanctuary with a fireplace."
            ),
            clues = listOf(
                "The suspect in the Conservatory carried the Lead Pipe.",
                "Victor Hale was not in the Study.",
                "Dr. Adrian Vale had the Poison Vial.",
                "Eleanor Voss was seen in the Library."
            ),
            hasLiar = false,
            solutionSuspect = "Dr. Adrian Vale",
            solutionWeapon = "Poison Vial",
            solutionLocation = "Study",
            solutionLiar = null,
            murderExplanation = "Dr. Adrian Vale carried the Poison Vial and was in the Study. The vial contained cyanide, and the disturbed desk suggested an attempted staging of a violent struggle after Sterling had already been poisoned. Vale killed Sterling because Sterling was ending his employment and terminating the research programme that had defined his career."
        ),
        Case(
            id = 2,
            title = "Whispers in the Observatory",
            difficulty = "Medium",
            story = "A prominent astronomer was found dead beside a collapsed telescope in an old mountaintop observatory. The observatory forms part of an isolated hilltop estate containing a chapel and an underground wine cellar. Investigators suspect that the damaged telescope was used to disguise an assault. Determine the culprit, weapon, and location.",
            suspects = listOf("Marcus Flint", "Selene Marlow", "Elias Crowe"),
            weapons = listOf("Brass Knuckles", "Heavy Candelabra", "Length of Rope"),
            locations = listOf("Observatory", "Wine Cellar", "Gothic Chapel"),
            suspectDescriptions = mapOf(
                "Marcus Flint" to "A retired officer with a violent temper and a long-standing grudge against the astronomer.",
                "Selene Marlow" to "A celebrated astrologer and fortune teller whose fabricated celestial predictions were about to be publicly exposed by the victim.",
                "Elias Crowe" to "A politician whose career could have been destroyed by the astronomer’s private records."
            ),
            weaponDescriptions = mapOf(
                "Brass Knuckles" to "Solid metal knuckles stamped with a military insignia.",
                "Heavy Candelabra" to "An iron candleholder, dented on one side.",
                "Length of Rope" to "A coarse hemp rope used to hang heavy tapestries."
            ),
            locationDescriptions = mapOf(
                "Observatory" to "The dome room containing the star charts and telescope.",
                "Wine Cellar" to "A damp underground cellar lined with dust-covered barrels.",
                "Gothic Chapel" to "A candlelit prayer room with heavy stained glass."
            ),
            clues = listOf(
                "Elias Crowe was in the Gothic Chapel.",
                "The Brass Knuckles were not in the Observatory.",
                "Marcus Flint did not carry the Length of Rope.",
                "The Length of Rope was found in the Gothic Chapel.",
                "Selene Marlow was seen entering the Observatory shortly before the astronomer’s death."
            ),
            hasLiar = false,
            solutionSuspect = "Selene Marlow",
            solutionWeapon = "Heavy Candelabra",
            solutionLocation = "Observatory",
            solutionLiar = null,
            murderExplanation = "Selene Marlow was in the Observatory with the Heavy Candelabra. The astronomer’s injuries matched the candelabra rather than the telescope, and Selene damaged the telescope to make the death appear accidental. Her motive was to prevent the public exposure of her fraudulent predictions."
        ),
        Case(
            id = 3,
            title = "The Silent Finale",
            difficulty = "Medium",
            story = "During a private musical gala at Blackwell Manor, the conductor collapsed as the orchestra reached its final note. The manor’s ballroom, billiard room, and attic were sealed immediately. Exactly one suspect is lying, while the other two statements are true. Reconstruct the evidence and identify the killer, weapon, location, and false witness.",
            suspects = listOf("Helena Blackwell", "Rowan Pierce", "Conrad Ashford"),
            weapons = listOf("Rusty Saber", "Suppressed Pistol", "Poisoned Wine"),
            locations = listOf("Ballroom", "Billiard Room", "Secret Attic"),
            suspectDescriptions = mapOf(
                "Helena Blackwell" to "A wealthy patron whose financial support kept the orchestra operating—and whose private arrangement with the conductor was close to becoming public.",
                "Rowan Pierce" to "A decorated former naval officer who blamed the conductor for exposing an incident that ended his military career.",
                "Conrad Ashford" to "An industrialist locked in a bitter ownership dispute with the conductor over the future of the venue."
            ),
            weaponDescriptions = mapOf(
                "Rusty Saber" to "A ceremonial sword taken from the theatre trophy wall.",
                "Suppressed Pistol" to "A quiet 9mm pistol equipped with a sound suppressor.",
                "Poisoned Wine" to "A goblet of expensive vintage spiked with belladonna."
            ),
            locationDescriptions = mapOf(
                "Ballroom" to "The grand dance floor, now empty and eerie.",
                "Billiard Room" to "A smoky parlor centering a large green felt table.",
                "Secret Attic" to "A dusty loft above the stage filled with old props."
            ),
            clues = listOf(
                "TWIST: Exactly ONE suspect is lying, and the other two are telling the truth.",
                "The Rusty Saber was found in the Secret Attic.",
                "Rowan Pierce was definitely in the Ballroom.",
                "The suspect in the Billiard Room did not have the Suppressed Pistol."
            ),
            hasLiar = true,
            statements = listOf(
                Statement("Helena Blackwell", "I was in the Billiard Room.", isLie = false),
                Statement("Rowan Pierce", "Helena Blackwell did not have the Rusty Saber.", isLie = false),
                Statement("Conrad Ashford", "I had the Poisoned Wine in the Secret Attic.", isLie = true)
            ),
            solutionSuspect = "Rowan Pierce",
            solutionWeapon = "Suppressed Pistol",
            solutionLocation = "Ballroom",
            solutionLiar = "Conrad Ashford",
            murderExplanation = "Helena Blackwell is in the Billiard Room, and Rowan Pierce is in the Ballroom. The Rusty Saber was found in the Secret Attic, so Conrad Ashford was there with the saber. Conrad’s claim that he had the Poisoned Wine is the single lie. Helena therefore has the Poisoned Wine, leaving Rowan with the Suppressed Pistol in the Ballroom. Rowan fired during the final surge of music, masking the sound."
        ),
        Case(
            id = 4,
            title = "The Curator’s Last Exhibit",
            difficulty = "Hard",
            story = "Museum curator Alistair Wren was found dead in the institution’s greenhouse shortly after a security alarm was triggered in the Antique Vault. A nearly invisible puncture wound suggests that his death was no accident. Exactly one of the three suspects has given a false statement. Reconstruct their movements, identify the liar, and determine who killed the curator, with what weapon, and where.",
            suspects = listOf("Garrett Thorne", "Dr. Mira Elgin", "Rosalie Crane"),
            weapons = listOf("Chloroform Rag", "Poison Dart", "Pocket Knife"),
            locations = listOf("Antique Vault", "Greenhouse", "Trophy Room"),
            suspectDescriptions = mapOf(
                "Garrett Thorne" to "A retired military archivist who claimed that one of the museum’s prized acquisitions had been taken from his family. A failed legal challenge left him heavily in debt.",
                "Dr. Mira Elgin" to "The museum’s conservation chemist, with authorised access to restricted storage rooms, preservation compounds, and the curator’s private research files.",
                "Rosalie Crane" to "A wealthy donor who demanded that the museum authenticate and display her family’s collection. The curator had recently uncovered evidence that several pieces were fraudulent."
            ),
            weaponDescriptions = mapOf(
                "Chloroform Rag" to "A folded cloth saturated with chloroform.",
                "Poison Dart" to "A small blowgun dart coated with a fast-acting toxin derived from plants cultivated in the museum greenhouse.",
                "Pocket Knife" to "A bone-handled folding knife recently used to cut through the Antique Vault’s security seal."
            ),
            locationDescriptions = mapOf(
                "Antique Vault" to "A climate-controlled chamber protected by an electronic security seal.",
                "Greenhouse" to "A humid glasshouse containing rare medicinal and poisonous plants.",
                "Trophy Room" to "A private exhibition room lined with hunting trophies and archived acquisition records."
            ),
            clues = listOf(
                "TWIST: Exactly one suspect is lying, and the other two are telling the truth.",
                "The Pocket Knife was found in the Antique Vault.",
                "Security records place Dr. Mira Elgin in the Trophy Room.",
                "Garrett Thorne was confirmed to be inside the Antique Vault."
            ),
            hasLiar = true,
            statements = listOf(
                Statement("Garrett Thorne", "I was carrying the Pocket Knife.", isLie = false),
                Statement("Dr. Mira Elgin", "Garrett Thorne was in the Greenhouse.", isLie = true),
                Statement("Rosalie Crane", "The Chloroform Rag was in the Trophy Room.", isLie = false)
            ),
            solutionSuspect = "Rosalie Crane",
            solutionWeapon = "Poison Dart",
            solutionLocation = "Greenhouse",
            solutionLiar = "Dr. Mira Elgin",
            murderExplanation = "Garrett Thorne was carrying the Pocket Knife, and the knife was found in the Antique Vault, confirming that Garrett was there. Dr. Mira Elgin’s claim that Garrett was in the Greenhouse is therefore the single false statement. Security records place Mira in the Trophy Room, and Rosalie truthfully identified the Chloroform Rag there, so Mira possessed it. That leaves Rosalie Crane in the Greenhouse with the Poison Dart. The dart’s toxin came from plants cultivated in the greenhouse. Rosalie killed Curator Alistair Wren before he could expose the fraudulent pieces in her family’s collection."
        )
    )
}
