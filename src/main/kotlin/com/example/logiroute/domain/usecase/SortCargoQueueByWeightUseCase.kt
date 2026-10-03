package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package

class SortCargoQueueByWeightUseCase {

    operator fun invoke(packages: List<Package>): List<Package> =
        quickSort(packages)

    private fun quickSort(packages: List<Package>): List<Package> {
        if (packages.size < 2) return packages

        val pivotWeight = packages[packages.size / 2].weight
        val heavier = packages.filter { it.weight > pivotWeight }
        val sameWeight = packages.filter { it.weight == pivotWeight }
        val lighter = packages.filter { it.weight < pivotWeight }

        return quickSort(heavier) +
                sameWeight +
                quickSort(lighter)
    }
}