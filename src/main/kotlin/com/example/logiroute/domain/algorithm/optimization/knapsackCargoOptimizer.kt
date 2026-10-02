package com.example.logiroute.domain.algorithm.optimization
import com.example.logiroute.domain.model.Package
class KnapsackCargoOptimizer {
    fun optimize(
        packages: List<Package>,
        maxCapacityKg: Double
    ): List<Package> {

        val capacity = maxCapacityKg.toInt()

        val grid = Array(packages.size + 1) {
            IntArray(capacity + 1)
        }

        // Build the 2D dynamic programming grid
        for (packageIndex in 1..packages.size) {

            val currentPackage = packages[packageIndex - 1]
            val packageWeight = currentPackage.weight.toInt()
            val priorityScore = getPriorityScore(currentPackage)

            for (currentCapacity in 0..capacity) {

                if (packageWeight <= currentCapacity) {

                    val skipPackage =
                        grid[packageIndex - 1][currentCapacity]

                    val takePackage =
                        priorityScore +
                                grid[packageIndex - 1][currentCapacity - packageWeight]

                    grid[packageIndex][currentCapacity] =
                        maxOf(skipPackage, takePackage)

                } else {
                    grid[packageIndex][currentCapacity] =
                        grid[packageIndex - 1][currentCapacity]
                }
            }
        }

        // Backtrack through the grid to find selected packages
        val selectedPackages = mutableListOf<Package>()
        var remainingCapacity = capacity

        for (packageIndex in packages.size downTo 1) {

            val packageWasSelected =
                grid[packageIndex][remainingCapacity] !=
                        grid[packageIndex - 1][remainingCapacity]

            if (packageWasSelected) {

                val selectedPackage =
                    packages[packageIndex - 1]

                selectedPackages.add(selectedPackage)

                remainingCapacity -=
                    selectedPackage.weight.toInt()
            }
        }

        return selectedPackages.reversed()
    }

    private fun getPriorityScore(
        packageItem: Package
    ): Int {
        return packageItem.priority.ordinal + MIN_PRIORITY_SCORE
    }

    companion object {
        private const val MIN_PRIORITY_SCORE = 1
    }
}