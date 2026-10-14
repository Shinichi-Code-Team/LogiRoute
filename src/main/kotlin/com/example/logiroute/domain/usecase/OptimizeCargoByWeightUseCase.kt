package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import kotlin.math.ceil
import kotlin.math.floor

class OptimizeCargoByWeightUseCase {

    operator fun invoke(
        packages: List<Package>,
        maxCapacityKg: Double,
        maxVolumeM3: Double? = null
    ): List<Package> {
        val weightLimit = toUnits(
            maxCapacityKg,
            "Weight capacity",
            roundUp = false
        )

        val volumeLimit = maxVolumeM3?.let {
            toUnits(it, "Volume capacity", roundUp = false)
        }

        var states = linkedMapOf(
            Capacity(0, 0) to Selection(score = 0, previous = null, index = null)
        )

        packages.forEachIndexed { index, packageItem ->
            val weight = toUnits(
                packageItem.weight,
                "Package weight",
                roundUp = true
            )

            val volume = if (volumeLimit != null) {
                val packageVolume = requireNotNull(packageItem.volumeM3) {
                    "Package ${packageItem.id} has no volume."
                }
                toUnits(packageVolume, "Package volume", roundUp = true)
            } else {
                0
            }

            val nextStates = LinkedHashMap(states)

            states.forEach { (used, selection) ->
                val nextWeight = used.weight + weight
                val nextVolume = used.volume + volume

                if (nextWeight > weightLimit) return@forEach
                if (volumeLimit != null && nextVolume > volumeLimit) {
                    return@forEach
                }

                val capacity = Capacity(nextWeight, nextVolume)
                val candidate = Selection(
                    score = selection.score + priorityScore(packageItem.priority),
                    previous = selection,
                    index = index
                )
                val existing = nextStates[capacity]

                if (existing == null || candidate.score > existing.score) {
                    nextStates[capacity] = candidate
                }
            }

            states = nextStates
        }

        val selectedIndexes = mutableListOf<Int>()
        var selection: Selection? = states.values.maxBy { it.score }

        while (selection != null) {
            selection.index?.let(selectedIndexes::add)
            selection = selection.previous
        }

        return selectedIndexes.asReversed().map(packages::get)
    }

    private fun priorityScore(priority: Priority): Int =
        when (priority) {
            Priority.LOW -> 1
            Priority.STANDARD -> 2
            Priority.URGENT -> 3
        }

    private fun toUnits(
        value: Double,
        field: String,
        roundUp: Boolean
    ): Int {
        require(value.isFinite() && value >= 0.0) {
            "$field must be finite and non-negative."
        }

        val scaled = value * UNITS_PER_UNIT
        require(scaled.isFinite() && scaled <= Int.MAX_VALUE - 1) {
            "$field is too large to optimize."
        }

        return if (roundUp) ceil(scaled).toInt() else floor(scaled).toInt()
    }

    private data class Capacity(
        val weight: Int,
        val volume: Int
    )

    private data class Selection(
        val score: Int,
        val previous: Selection?,
        val index: Int?
    )

    private companion object {
        const val UNITS_PER_UNIT = 1_000
    }
}