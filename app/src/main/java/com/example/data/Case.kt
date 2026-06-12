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
    val murderExplanation: String
)

object CaseSeeds {
    val cases = listOf(
        Case(
            id = 1,
            title = "The Bloodstained Study",
            difficulty = "Easy",
            story = "Lord Sterling was found dead in his study. The pristine mansion has fallen silent. The local constabulary has locked down three main suspects, weapons, and locations. Your job, Detective, is to examine the clues, fill out your logic grid, and state exactly who committed the murder, with what, and where.",
            suspects = listOf("Lord Crimson", "Dame Obsidian", "Professor Celadon"),
            weapons = listOf("Antique Dagger", "Lead Pipe", "Poison Vial"),
            locations = listOf("Study", "Conservatory", "Library"),
            suspectDescriptions = mapOf(
                "Lord Crimson" to "The bitter business rival of Lord Sterling. Claims he was looking at exotic orchids.",
                "Dame Obsidian" to "Sterling's wealthy sister, who stands to inherit the entire fortune. Calm and calculating.",
                "Professor Celadon" to "Sterling's private chemist who was recently threatened with firing."
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
                "Lord Crimson was not in the Study.",
                "Professor Celadon had the Poison Vial.",
                "Dame Obsidian was enjoying a book in the Library."
            ),
            hasLiar = false,
            solutionSuspect = "Professor Celadon",
            solutionWeapon = "Poison Vial",
            solutionLocation = "Study",
            murderExplanation = "Professor Celadon carried the Poison Vial and was in the Study. The coroner's report states Lord Sterling was dead from cyanide poisoning before any struggle took place. Professor Celadon committed the deed to save his chemical research!"
        ),
        Case(
            id = 2,
            title = "Whispers in the Observatory",
            difficulty = "Medium",
            story = "A prominent astronomer was found dead under a collapsed telescope at the old high-altitude Observatory. There were signs of a struggle. Review the physical clues and the suspects' movements to expose the killer.",
            suspects = listOf("Major Vermilion", "Madame Indigo", "Senator Gold"),
            weapons = listOf("Brass Knuckles", "Heavy Candelabra", "Rope Strand"),
            locations = listOf("Observatory", "Wine Cellar", "Gothic Chapel"),
            suspectDescriptions = mapOf(
                "Major Vermilion" to "A retired military officer with a fiery temper. He hated the astronomer's predictions.",
                "Madame Indigo" to "An eccentric fortune teller. Claims the stars forced her hand.",
                "Senator Gold" to "A corrupt politician who was blackmailed by the astronomer."
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
                "Senator Gold was in the Gothic Chapel.",
                "The Brass Knuckles were not in the Observatory.",
                "Major Vermilion did not carry the Rope Strand.",
                "The Rope Strand was found in the Gothic Chapel.",
                "Madame Indigo was seen near the telescope in the Observatory."
            ),
            hasLiar = false,
            solutionSuspect = "Madame Indigo",
            solutionWeapon = "Heavy Candelabra",
            solutionLocation = "Observatory",
            murderExplanation = "Madame Indigo was in the Observatory with the Heavy Candelabra. Forensic evidence proved the astronomer died of severe blunt-force trauma corresponding exactly to the iron candelabra. Madame Indigo silenced him to prevent her psychic fraud from being exposed!"
        ),
        Case(
            id = 3,
            title = "Intermission in the Ballroom",
            difficulty = "Medium",
            story = "The conductor took a bow and fell dead. Exactly ONE of the suspects is lying during interrogation to mask their guilt. Cross-examine their claims, check the verified physical clues, and expose the liar!",
            suspects = listOf("Duchess Silver", "Captain Scarlett", "Baron Cobalt"),
            weapons = listOf("Rusty Saber", "Silenced Gun", "Poisoned Wine"),
            locations = listOf("Ballroom", "Billiard Room", "Secret Attic"),
            suspectDescriptions = mapOf(
                "Duchess Silver" to "An elegant socialite who claims she was playing billiards all evening.",
                "Captain Scarlett" to "A decorated naval commander with a secretive past.",
                "Baron Cobalt" to "An influential industrialist who threatened to shut down the theatre."
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
                "Captain Scarlett was definitely in the Ballroom.",
                "The suspect in the Billiard Room did not have the Silenced Gun."
            ),
            hasLiar = true,
            statements = listOf(
                Statement("Duchess Silver", "I was in the Billiard Room.", isLie = false),
                Statement("Captain Scarlett", "Duchess Silver did not have the Rusty Saber.", isLie = false),
                Statement("Baron Cobalt", "I had the Poisoned Wine in the Secret Attic.", isLie = true)
            ),
            solutionSuspect = "Captain Scarlett",
            solutionWeapon = "Silenced Gun",
            solutionLocation = "Ballroom",
            murderExplanation = "Duchess Silver is in the Billiard Room (True) and Captain Scarlett is in the Ballroom (True). Since the Rusty Saber was in the Secret Attic, Baron Cobalt (in the Attic) had the Rusty Saber. Therefore, Baron Cobalt's statement that he had the Poisoned Wine is the LIE. Since Duchess Silver did not have the Silenced Gun, she must have had the Poisoned Wine in the Billiard Room. That leaves Captain Scarlett in the Ballroom with the Silenced Gun. The conductor was shot silently during the opera's climax by Captain Scarlett!"
        ),
        Case(
            id = 4,
            title = "The Poisoned Antique",
            difficulty = "Hard",
            story = "The curator's secure vault was entered, and a prized urn was contaminated with a deadly aerosol toxin. One of his close associates is lying during the investigation. Crack the grid, spot the liar, and make your accusation!",
            suspects = listOf("General Amber", "Dr. Violet", "Madame Rose"),
            weapons = listOf("Chloroform Rag", "Poison Dart", "Pocket Knife"),
            locations = listOf("Antique Vault", "Greenhouse", "Trophy Room"),
            suspectDescriptions = mapOf(
                "General Amber" to "A stern war veteran who collects rare historical weapons.",
                "Dr. Violet" to "The resident antiquities expert with deep library access.",
                "Madame Rose" to "A wealthy donor who claims she was admiring orchids."
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
                "Dr. Violet was officially stationed in the Trophy Room."
            ),
            hasLiar = true,
            statements = listOf(
                Statement("General Amber", "I had the Pocket Knife.", isLie = false),
                Statement("Dr. Violet", "General Amber was in the Greenhouse.", isLie = true),
                Statement("Madame Rose", "The Chloroform Rag was in the Trophy Room.", isLie = false)
            ),
            solutionSuspect = "Madame Rose",
            solutionWeapon = "Poison Dart",
            solutionLocation = "Greenhouse",
            murderExplanation = "General Amber had the Pocket Knife (True). Since the Pocket Knife was in the Antique Vault, General Amber was in the Antique Vault. This proves Dr. Violet lied by claiming General Amber was in the Greenhouse! Madame Rose told the truth (Chloroform Rag was in the Trophy Room). Since Dr. Violet was in the Trophy Room, Dr. Violet had the Chloroform Rag. This leaves Madame Rose in the Greenhouse with the Poison Dart. The collector suffered a sudden, silent puncture wound from Madame Rose using the Poison Dart!"
        )
    )
}
