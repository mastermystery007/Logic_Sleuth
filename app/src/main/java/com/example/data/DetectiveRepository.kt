package com.example.data

import kotlinx.coroutines.flow.Flow

class DetectiveRepository(private val dao: DetectiveDao) {

    val completedCases: Flow<List<CompletedCase>> = dao.getCompletedCases()

    fun getGridCellsForCase(caseId: Int): Flow<List<GridCellState>> =
        dao.getGridCellsForCase(caseId)

    suspend fun saveGridCell(cell: GridCellState) {
        dao.insertGridCell(cell)
    }

    suspend fun saveGridCells(cells: List<GridCellState>) {
        dao.insertGridCells(cells)
    }

    suspend fun clearGridForCase(caseId: Int) {
        dao.clearGridForCase(caseId)
    }

    fun getNotesForCase(caseId: Int): Flow<CaseNotes?> =
        dao.getNotesForCase(caseId)

    suspend fun saveCheckedClues(progress: CaseNotes) {
        dao.insertNotes(progress)
    }

    suspend fun markCaseCompleted(caseId: Int) {
        dao.insertCompletedCase(CompletedCase(caseId = caseId))
    }

    suspend fun removeCompletedCase(caseId: Int) {
        dao.removeCompletedCase(caseId)
    }

    fun getCases(): List<Case> = CaseSeeds.cases

    fun getCaseById(id: Int): Case? = CaseSeeds.cases.find { it.id == id }
}
