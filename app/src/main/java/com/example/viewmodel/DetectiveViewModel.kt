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

    // Selection filters for Dashboard
    private val _difficultyFilter = MutableStateFlow("All")
    val difficultyFilter: StateFlow<String> = _difficultyFilter.asStateFlow()

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
    val activeNotes: StateFlow<String> = _activeCaseId
        .flatMapLatest { id ->
            if (id == null) flowOf(null) else repository.getNotesForCase(id)
        }
        .map { it?.notes ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

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
        _accusationResult.value = AccusationResult.None
    }

    fun setDifficultyFilter(filter: String) {
        _difficultyFilter.value = filter
    }

    // Grid Cell Selection: empty "" -> "X" -> "O" -> empty ""
    fun toggleGridCell(row: Int, col: Int) {
        val caseId = _activeCaseId.value ?: return
        
        viewModelScope.launch {
            // Read active grid cell from flow or direct query safely
            val currentState = activeGrid.value
            val pair = Pair(row, col)
            val nextMark = when (currentState[pair]) {
                "X" -> "O"
                "O" -> ""
                else -> "X"
            }

            if (nextMark.isEmpty()) {
                val cellState = GridCellState(
                    cellId = "${caseId}_${row}_${col}",
                    caseId = caseId,
                    row = row,
                    col = col,
                    mark = ""
                )
                repository.saveGridCell(cellState)
            } else {
                if (nextMark == "O") {
                    val cellsToSave = mutableListOf<GridCellState>()
                    
                    // Main tapped cell is "O"
                    cellsToSave.add(
                        GridCellState("${caseId}_${row}_${col}", caseId, row, col, "O")
                    )

                    // Find subgrid boundary
                    val rowSubgrid = if (row in 0..2) 0..2 else 3..5
                    val colSubgrid = if (col in 0..2) 0..2 else 3..5

                    // Auto-cross other cells in same row within the subgrid column bounds
                    for (c in colSubgrid) {
                        if (c != col) {
                            val currentC = currentState[Pair(row, c)]
                            if (currentC != "O") {
                                cellsToSave.add(
                                    GridCellState("${caseId}_${row}_${c}", caseId, row, c, "X")
                                )
                            }
                        }
                    }

                    // Auto-cross other cells in same column within the subgrid row bounds
                    for (r in rowSubgrid) {
                        if (r != row) {
                            val currentR = currentState[Pair(r, col)]
                            if (currentR != "O") {
                                cellsToSave.add(
                                    GridCellState("${caseId}_${r}_${col}", caseId, r, col, "X")
                                )
                            }
                        }
                    }

                    repository.saveGridCells(cellsToSave)
                } else {
                    // Regular "X" click
                    val cellState = GridCellState(
                        cellId = "${caseId}_${row}_${col}",
                        caseId = caseId,
                        row = row,
                        col = col,
                        mark = nextMark
                    )
                    repository.saveGridCell(cellState)
                }
            }
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

    fun makeAccusation() {
        val case = activeCase.value ?: return
        val suspect = _chosenSuspect.value
        val weapon = _chosenWeapon.value
        val location = _chosenLocation.value

        if (suspect.isEmpty() || weapon.isEmpty() || location.isEmpty()) {
            _accusationResult.value = AccusationResult.Failure
            return
        }

        if (suspect == case.solutionSuspect &&
            weapon == case.solutionWeapon &&
            location == case.solutionLocation
        ) {
            _accusationResult.value = AccusationResult.Success
            viewModelScope.launch {
                repository.markCaseCompleted(case.id)
            }
        } else {
            _accusationResult.value = AccusationResult.Failure
        }
    }

    fun saveNotes(notesText: String) {
        val caseId = _activeCaseId.value ?: return
        viewModelScope.launch {
            repository.saveNotes(CaseNotes(caseId, notesText))
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
