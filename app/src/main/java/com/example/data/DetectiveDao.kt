package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DetectiveDao {
    @Query("SELECT * FROM completed_cases")
    fun getCompletedCases(): Flow<List<CompletedCase>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedCase(case: CompletedCase)

    @Query("DELETE FROM completed_cases WHERE caseId = :caseId")
    suspend fun removeCompletedCase(caseId: Int)

    @Query("SELECT * FROM active_grid_cells WHERE caseId = :caseId")
    fun getGridCellsForCase(caseId: Int): Flow<List<GridCellState>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGridCell(cell: GridCellState)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGridCells(cells: List<GridCellState>)

    @Query("DELETE FROM active_grid_cells WHERE caseId = :caseId")
    suspend fun clearGridForCase(caseId: Int)

    @Query("SELECT * FROM case_notes WHERE caseId = :caseId")
    fun getNotesForCase(caseId: Int): Flow<CaseNotes?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: CaseNotes)
}
