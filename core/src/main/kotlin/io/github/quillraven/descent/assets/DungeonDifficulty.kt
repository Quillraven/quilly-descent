package io.github.quillraven.descent.assets

enum class DungeonDifficulty(
    val width: Int,
    val height: Int,
    val criticalPathLength: IntRange,
    val numBranches: IntRange,
    val branchLength: IntRange,
) {
    EASY(
        width = 3,
        height = 3,
        criticalPathLength = 3..4,
        numBranches = 0..1,
        branchLength = 1..2
    ),

    MEDIUM(
        width = 5,
        height = 5,
        criticalPathLength = 6..8,
        numBranches = 1..3,
        branchLength = 2..3
    ),

    HARD(
        width = 8,
        height = 8,
        criticalPathLength = 12..15,
        numBranches = 4..6,
        branchLength = 3..5
    ),
}