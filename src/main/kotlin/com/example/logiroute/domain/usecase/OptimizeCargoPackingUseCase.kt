package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import kotlin.math.ceil
import kotlin.math.floor

class OptimizeCargoPackingUseCase {

    operator fun invoke(
        packages: List<Package>,
        maxCapacityKg: Double
    ): List<Package> {
        require(maxCapacityKg.isFinite() && maxCapacityKg >= 0.0) {
            "Capacity must be finite and non-negative."
        }

        val capacityUnits = toUnits(maxCapacityKg, roundUp = false)
        val packageWeights = packages.map { packageItem ->
            require(packageItem.weight.isFinite() && packageItem.weight > 0.0) {
                "Package weight must be finite and positive."
            }
            toUnits(packageItem.weight, roundUp = true)
        }

        val grid = packages.indices.scan(
            List(capacityUnits + 1) { 0 }
        ) { previousRow, packageIndex ->
            val weight = packageWeights[packageIndex]
            val score = priorityScore(packages[packageIndex].priority)

            previousRow.mapIndexed { capacity, skipScore ->
                if (weight <= capacity) {
                    val takeScore =
                        score + previousRow[capacity - weight]
                    maxOf(skipScore, takeScore)
                } else {
                    skipScore
                }
            }
        }

        val selectedInReverse = packages.indices
            .reversed()
            .fold(capacityUnits to emptyList<Package>()) {
                    (remainingCapacity, selected), packageIndex ->
                if (grid[packageIndex + 1][remainingCapacity] >
                    grid[packageIndex][remainingCapacity]
                ) {
                    (remainingCapacity - packageWeights[packageIndex]) to
                            (selected + packages[packageIndex])
                } else {
                    remainingCapacity to selected
                }
            }
            .second

        return selectedInReverse.reversed()
    }

    private fun priorityScore(priority: Priority): Int =
        when (priority) {
            Priority.LOW -> 1
            Priority.STANDARD -> 2
            Priority.URGENT -> 3
        }

    private fun toUnits(weightKg: Double, roundUp: Boolean): Int {
        val scaledWeight = weightKg * UNITS_PER_KG

        require(scaledWeight.isFinite() && scaledWeight <= Int.MAX_VALUE - 1) {
            "Weight is too large to optimize."
        }

        return if (roundUp) ceil(scaledWeight).toInt()
        else floor(scaledWeight).toInt()
    }

    private companion object {
        const val UNITS_PER_KG = 1_000
    }
}