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
            title = "The Bloodstained Study",
            difficulty = "Easy",
            story = "Lord Sterling was found dead in his study. The pristine mansion has fallen silent. The local constabulary has locked down three main suspects, weapons, and locations. Your job, Detective, is to examine the clues, fill out your logic grid, and state exactly who committed the murder, with what, and where.",
            suspects = listOf("Victor Hale", "Eleanor Voss", "Dr. Adrian Vale"),
            weapons = listOf("Antique Dagger", "Lead Pipe", "Poison Vial"),
            locations = listOf("Study", "Conservatory", "Library"),
            suspectDescriptions = mapOf(
                "Victor Hale" to "A polished investor who had a bitter business dispute with Sterling. He claims he spent the evening near the conservatory.",
                "Eleanor Voss" to "Sterling’s estranged sister and the main beneficiary of his estate. Calm, precise, and difficult to read.",
                "Dr. Adrian Vale" to "Sterling’s private chemist, recently threatened with dismissal after a failed experiment."
            ),
            weaponDescriptions = mapOf(
                "Antique Dagger" to "An ornate medieval blade taken from the hallway display.",
                "Lead Pipe" to "A heavy pipe covered in plumbing grease.",
                "Poison Vial" to "A concentrated compound of deadly cyanide."
            ),
            locationDescriptions = mapOf(
                "Study" to "The scene of the crime, filled with books and overturned furniture.",
                "Conservatory" to "A warm glasshouse full of tropical ferns.",
                "Library" to "A quiet sanctuary with a fireplace."
            ),
            clues = listOf(
                "The suspect in the Conservatory carried the Lead Pipe.",
                "Victor Hale was not in the Study.",
                "Dr. Adrian Vale had the Poison Vial.",
                "Eleanor Voss was enjoying a book in the Library."
            ),
            hasLiar = false,
            solutionSuspect = "Dr. Adrian Vale",
            solutionWeapon = "Poison Vial",
            solutionLocation = "Study",
            solutionLiar = null,
            murderExplanation = "Dr. Adrian Vale carried the Poison Vial and was in the Study. The coroner's report states Lord Sterling was dead from cyanide poisoning before any struggle took place. Dr. Adrian Vale committed the deed to save his chemical research!"
        ),
        Case(
            id = 2,
            title = "Whispers in the Observatory",
            difficulty = "Medium",
            story = "A prominent astronomer was found dead under a collapsed telescope at the old high-altitude Observatory. There were signs of a struggle. Review the physical clues and the suspects' movements to expose the killer.",
            suspects = listOf("Marcus Flint", "Selene Marlow", "Elias Crowe"),
            weapons = listOf("Brass Knuckles", "Heavy Candelabra", "Rope Strand"),
            locations = listOf("Observatory", "Wine Cellar", "Gothic Chapel"),
            suspectDescriptions = mapOf(
                "Marcus Flint" to "A retired officer with a violent temper and a long-standing grudge against the astronomer.",
                "Selene Marlow" to "A theatrical fortune teller whose predictions were about to be exposed as fraud.",
                "Elias Crowe" to "A politician whose career could have been destroyed by the astronomer’s private records."
            ),
            weaponDescriptions = mapOf(
                "Brass Knuckles" to "Solid metal knuckles stamped with a military insignia.",
                "Heavy Candelabra" to "An iron candleholder, dented on one side.",
                "Rope Strand" to "A coarse hemp rope used to hang heavy tapestries."
            ),
            locationDescriptions = mapOf(
                "Observatory" to "The dome room containing the star charts and telescope.",
                "Wine Cellar" to "A damp underground cellar lined with dust-covered barrels.",
                "Gothic Chapel" to "A candlelit prayer room with heavy stained glass."
            ),
            clues = listOf(
                "Elias Crowe was in the Gothic Chapel.",
                "The Brass Knuckles were not in the Observatory.",
                "Marcus Flint did not carry the Rope Strand.",
                "The Rope Strand was found in the Gothic Chapel.",
                "Selene Marlow was seen near the telescope in the Observatory."
            ),
            hasLiar = false,
            solutionSuspect = "Selene Marlow",
            solutionWeapon = "Heavy Candelabra",
            solutionLocation = "Observatory",
            solutionLiar = null,
            murderExplanation = "Selene Marlow was in the Observatory with the Heavy Candelabra. Forensic evidence proved the astronomer died of severe blunt-force trauma corresponding exactly to the iron candelabra. Selene Marlow silenced him to prevent her psychic fraud from being exposed!"
        ),
        Case(
            id = 3,
            title = "Intermission in the Ballroom",
            difficulty = "Medium",
            story = "The conductor took a bow and fell dead. Exactly ONE of the suspects is lying during interrogation to mask their guilt. Cross-examine their claims, check the verified physical clues, and expose the liar!",
            suspects = listOf("Helena Blackwell", "Rowan Pierce", "Conrad Ashford"),
            weapons = listOf("Rusty Saber", "Silenced Gun", "Poisoned Wine"),
            locations = listOf("Ballroom", "Billiard Room", "Secret Attic"),
            suspectDescriptions = mapOf(
                "Helena Blackwell" to "A wealthy patron of the theatre who says she spent the evening in the billiard room.",
                "Rowan Pierce" to "A decorated former naval officer with a hidden connection to the conductor.",
                "Conrad Ashford" to "An industrialist who had threatened to shut the theatre down."
            ),
            weaponDescriptions = mapOf(
                "Rusty Saber" to "A ceremonial sword taken from the theatre trophy wall.",
                "Silenced Gun" to "A quiet 9mm pistol equipped with a sound suppressor.",
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
                "The suspect in the Billiard Room did not have the Silenced Gun."
            ),
            hasLiar = true,
            statements = listOf(
                Statement("Helena Blackwell", "I was in the Billiard Room.", isLie = false),
                Statement("Rowan Pierce", "Helena Blackwell did not have the Rusty Saber.", isLie = false),
                Statement("Conrad Ashford", "I had the Poisoned Wine in the Secret Attic.", isLie = true)
            ),
            solutionSuspect = "Rowan Pierce",
            solutionWeapon = "Silenced Gun",
            solutionLocation = "Ballroom",
            solutionLiar = "Conrad Ashford",
            murderExplanation = "Helena Blackwell is in the Billiard Room (True) and Rowan Pierce is in the Ballroom (True). Since the Rusty Saber was in the Secret Attic, Conrad Ashford (in the Attic) had the Rusty Saber. Therefore, Conrad Ashford's statement that he had the Poisoned Wine is the LIE. Since Helena Blackwell did not have the Silenced Gun, she must have had the Poisoned Wine in the Billiard Room. That leaves Rowan Pierce in the Ballroom with the Silenced Gun. The conductor was shot silently during the opera's climax by Rowan Pierce!"
        ),
        Case(
            id = 4,
            title = "The Poisoned Antique",
            difficulty = "Hard",
            story = "The curator's secure vault was entered, and a prized urn was contaminated with a deadly aerosol toxin. One of his close associates is lying during the investigation. Crack the grid, spot the liar, and make your accusation!",
            suspects = listOf("Garrett Thorne", "Dr. Mira Elgin", "Rosalie Crane"),
            weapons = listOf("Chloroform Rag", "Poison Dart", "Pocket Knife"),
            locations = listOf("Antique Vault", "Greenhouse", "Trophy Room"),
            suspectDescriptions = mapOf(
                "Garrett Thorne" to "A retired military archivist whose finances collapsed after a failed auction.",
                "Dr. Mira Elgin" to "A museum researcher with access to restricted catalogs and preservation chemicals.",
                "Rosalie Crane" to "A major donor whose promised gift depended on controlling the curator’s next exhibit."
            ),
            weaponDescriptions = mapOf(
                "Chloroform Rag" to "A cloth soaked in strong anesthetic vapors.",
                "Poison Dart" to "A tiny needle tipped with virulent exotic plant venom.",
                "Pocket Knife" to "A folding bone-handled blade stained with grease."
            ),
            locationDescriptions = mapOf(
                "Antique Vault" to "The double-locked high security chamber.",
                "Greenhouse" to "A humid glasshouse filled with poisonous and exotic plants.",
                "Trophy Room" to "A display hall lined with hunting trophies and old catalogs."
            ),
            clues = listOf(
                "TWIST: Exactly ONE suspect is lying, and the other two are telling the truth.",
                "The Pocket Knife was found in the Antique Vault.",
                "Dr. Mira Elgin was officially stationed in the Trophy Room."
            ),
            hasLiar = true,
            statements = listOf(
                Statement("Garrett Thorne", "I had the Pocket Knife.", isLie = false),
                Statement("Dr. Mira Elgin", "Garrett Thorne was in the Greenhouse.", isLie = true),
                Statement("Rosalie Crane", "The Chloroform Rag was in the Trophy Room.", isLie = false)
            ),
            solutionSuspect = "Rosalie Crane",
            solutionWeapon = "Poison Dart",
            solutionLocation = "Greenhouse",
            solutionLiar = "Dr. Mira Elgin",
            murderExplanation = "Garrett Thorne had the Pocket Knife (True). Since the Pocket Knife was in the Antique Vault, Garrett Thorne was in the Antique Vault. This proves Dr. Mira Elgin lied by claiming Garrett Thorne was in the Greenhouse! Rosalie Crane told the truth (Chloroform Rag was in the Trophy Room). Since Dr. Mira Elgin was in the Trophy Room, Dr. Mira Elgin had the Chloroform Rag. This leaves Rosalie Crane in the Greenhouse with the Poison Dart. The collector suffered a sudden, silent puncture wound from Rosalie Crane using the Poison Dart!"
        )
    )
}
