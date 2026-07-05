package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "completed_cases")
data class CompletedCase(
    @PrimaryKey val caseId: Int,
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "active_grid_cells")
data class GridCellState(
    @PrimaryKey val cellId: String, // format: "caseId_row_col"
    val caseId: Int,
    val row: Int,
    val col: Int,
    val mark: String // "", "X", "O"
)

@Entity(tableName = "case_notes")
data class CaseNotes(
    @PrimaryKey val caseId: Int,
    // Reuses the legacy notes column to persist comma-separated checked clue indices.
    val notes: String
)
