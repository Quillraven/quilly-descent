package io.github.quillraven.descent.assets

import io.github.quillraven.descent.procgen.IntVector2

enum class DungeonDifficulty(
    val dimension: IntVector2,
    val criticalPathLength: IntRange,
    val numBranches: IntRange,
    val branchLength: IntRange,
) {
    EASY(
        dimension = IntVector2(3, 3),
        criticalPathLength = 3..4,
        numBranches = 0..1,
        branchLength = 1..2
    ),

    MEDIUM(
        dimension = IntVector2(5, 5),
        criticalPathLength = 6..8,
        numBranches = 1..3,
        branchLength = 2..3
    ),

    HARD(
        dimension = IntVector2(8, 8),
        criticalPathLength = 12..15,
        numBranches = 4..6,
        branchLength = 3..5
    ),
}