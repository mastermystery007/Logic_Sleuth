package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface AccusationResult {
    object None : AccusationResult
    object Success : AccusationResult
    object Failure : AccusationResult
}

class DetectiveViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DetectiveRepository

    init {
        val database = DetectiveDatabase.getDatabase(application)
        repository = DetectiveRepository(database.detectiveDao())
    }

    // List of all cases available
    val cases: List<Case> = repository.getCases()

    // Set of complete case IDs
    val completedCaseIds: StateFlow<Set<Int>> = repository.completedCases
        .map { list -> list.map { it.caseId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    // Currently playing state
    private val _activeCaseId = MutableStateFlow<Int?>(null)
    val activeCaseId: StateFlow<Int?> = _activeCaseId.asStateFlow()

    private val _chosenSuspect = MutableStateFlow("")
    val chosenSuspect: StateFlow<String> = _chosenSuspect.asStateFlow()

    private val _chosenWeapon = MutableStateFlow("")
    val chosenWeapon: StateFlow<String> = _chosenWeapon.asStateFlow()

    private val _chosenLocation = MutableStateFlow("")
    val chosenLocation: StateFlow<String> = _chosenLocation.asStateFlow()

    private val _chosenLiar = MutableStateFlow("")
    val chosenLiar: StateFlow<String> = _chosenLiar.asStateFlow()

    private val _accusationResult = MutableStateFlow<AccusationResult>(AccusationResult.None)
    val accusationResult: StateFlow<AccusationResult> = _accusationResult.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeCase: StateFlow<Case?> = _activeCaseId
        .map { id -> id?.let { repository.getCaseById(it) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeGrid: StateFlow<Map<Pair<Int, Int>, String>> = _activeCaseId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else repository.getGridCellsForCase(id)
        }
        .map { list -> list.associate { Pair(it.row, it.col) to it.mark } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    @OptIn(ExperimentalCoroutinesApi::class)
    val checkedClues: StateFlow<Set<Int>> = _activeCaseId
        .flatMapLatest { id ->
            if (id == null) flowOf(null) else repository.getNotesForCase(id)
        }
        .map { progress ->
            progress?.notes
                ?.split(",")
                ?.mapNotNull { it.toIntOrNull() }
                ?.toSet()
                ?: emptySet()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    @OptIn(ExperimentalCoroutinesApi::class)
    val isActiveCaseCompleted: StateFlow<Boolean> = combine(_activeCaseId, completedCaseIds) { activeId, completedIds ->
        activeId != null && completedIds.contains(activeId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun selectCase(caseId: Int?) {
        _activeCaseId.value = caseId
        // Reset local accusation drafts for this case
        _chosenSuspect.value = ""
        _chosenWeapon.value = ""
        _chosenLocation.value = ""
        _chosenLiar.value = ""
        _accusationResult.value = AccusationResult.None
    }

    // Grid layout:
    // rows 0..2 = locations, rows 3..5 = weapons
    // cols 0..2 = suspects, cols 3..5 = weapons
    // active blocks: Location × Suspect, Location × Weapon, Weapon × Suspect
    // bottom-right Weapon × Weapon block is disabled.
    private fun isLocationSuspectCell(row: Int, col: Int): Boolean =
        row in 0..2 && col in 0..2

    private fun isLocationWeaponCell(row: Int, col: Int): Boolean =
        row in 0..2 && col in 3..5

    private fun isWeaponSuspectCell(row: Int, col: Int): Boolean =
        row in 3..5 && col in 0..2

    private fun isPlayableCell(row: Int, col: Int): Boolean =
        isLocationSuspectCell(row, col) ||
            isLocationWeaponCell(row, col) ||
            isWeaponSuspectCell(row, col)

    // Grid Cell Selection: unknown "" -> "X" -> "O" -> unknown ""
    fun toggleGridCell(row: Int, col: Int) {
        val caseId = _activeCaseId.value ?: return
        if (!isPlayableCell(row, col)) return

        viewModelScope.launch {
            val currentState = activeGrid.value
            val nextMark = when (currentState[Pair(row, col)]) {
                "X" -> "O"
                "O" -> ""
                else -> "X"
            }
            val cellsToSave = linkedMapOf<Pair<Int, Int>, GridCellState>()

            fun queue(r: Int, c: Int, mark: String) {
                if (!isPlayableCell(r, c)) return
                cellsToSave[Pair(r, c)] = GridCellState("${caseId}_${r}_${c}", caseId, r, c, mark)
            }

            queue(row, col, nextMark)

            if (nextMark == "O") {
                val rowGroup = when {
                    isLocationSuspectCell(row, col) -> 0..2
                    isLocationWeaponCell(row, col) -> 0..2
                    isWeaponSuspectCell(row, col) -> 3..5
                    else -> 0..-1
                }
                val colGroup = when {
                    isLocationSuspectCell(row, col) -> 0..2
                    isLocationWeaponCell(row, col) -> 3..5
                    isWeaponSuspectCell(row, col) -> 0..2
                    else -> 0..-1
                }

                for (c in colGroup) {
                    if (c != col && currentState[Pair(row, c)] != "O") queue(row, c, "X")
                }
                for (r in rowGroup) {
                    if (r != row && currentState[Pair(r, col)] != "O") queue(r, col, "X")
                }
            }

            repository.saveGridCells(cellsToSave.values.toList())
        }
    }

    fun selectAccusationSuspect(suspect: String) {
        _chosenSuspect.value = suspect
        _accusationResult.value = AccusationResult.None
    }

    fun selectAccusationWeapon(weapon: String) {
        _chosenWeapon.value = weapon
        _accusationResult.value = AccusationResult.None
    }

    fun selectAccusationLocation(location: String) {
        _chosenLocation.value = location
        _accusationResult.value = AccusationResult.None
    }

    fun chooseLiar(liar: String) {
        _chosenLiar.value = liar
        _accusationResult.value = AccusationResult.None
    }

    fun makeAccusation() {
        val case = activeCase.value ?: return
        val suspect = _chosenSuspect.value
        val weapon = _chosenWeapon.value
        val location = _chosenLocation.value
        val liar = _chosenLiar.value

        if (suspect.isEmpty() || weapon.isEmpty() || location.isEmpty() || (case.hasLiar && liar.isEmpty())) {
            _accusationResult.value = AccusationResult.Failure
            return
        }

        val basicAccusationCorrect = suspect == case.solutionSuspect &&
            weapon == case.solutionWeapon &&
            location == case.solutionLocation
        val liarCorrect = !case.hasLiar || liar == case.solutionLiar

        if (basicAccusationCorrect && liarCorrect) {
            _accusationResult.value = AccusationResult.Success
            viewModelScope.launch {
                repository.markCaseCompleted(case.id)
            }
        } else {
            _accusationResult.value = AccusationResult.Failure
        }
    }

    fun toggleClueChecked(index: Int) {
        val caseId = _activeCaseId.value ?: return
        viewModelScope.launch {
            val nextChecked = checkedClues.value.toMutableSet().apply {
                if (!add(index)) remove(index)
            }
            repository.saveCheckedClues(CaseNotes(caseId, nextChecked.sorted().joinToString(",")))
        }
    }

    fun resetCheckedClues() {
        val caseId = _activeCaseId.value ?: return
        viewModelScope.launch {
            repository.saveCheckedClues(CaseNotes(caseId, ""))
        }
    }

    fun resetGrid() {
        val caseId = _activeCaseId.value ?: return
        viewModelScope.launch {
            repository.clearGridForCase(caseId)
        }
    }

    fun clearCaseCompletion() {
        val caseId = _activeCaseId.value ?: return
        viewModelScope.launch {
            repository.removeCompletedCase(caseId)
        }
    }
}
