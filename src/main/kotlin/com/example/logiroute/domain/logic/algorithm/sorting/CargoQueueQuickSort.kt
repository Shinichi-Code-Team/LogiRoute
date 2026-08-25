package com.example.logiroute.domain.logic.algorithm.sorting

import com.example.logiroute.domain.model.Package

class CargoQueueQuickSort {

    fun sortByWeightDescending(
        packages: MutableList<Package>
    ) {
        if (packages.size < 2) return

        quickSort(
            packages,
            packages.indices.first,
            packages.lastIndex
        )
    }

    private fun quickSort(
        packages: MutableList<Package>,
        startIndex: Int,
        endIndex: Int
    ) {
        if (startIndex >= endIndex) return

        val pivotFinalIndex =
            partition(packages, startIndex, endIndex)

        quickSort(
            packages,
            startIndex,
            pivotFinalIndex - 1
        )

        quickSort(
            packages,
            pivotFinalIndex + 1,
            endIndex
        )
    }

    private fun partition(
        packages: MutableList<Package>,
        startIndex: Int,
        endIndex: Int
    ): Int {

        val pivotWeight = packages[endIndex].weight
        var lastLargerPackageIndex = startIndex - 1

        for (currentIndex in startIndex until endIndex) {

            if (packages[currentIndex].weight > pivotWeight) {
                lastLargerPackageIndex++

                swapPackages(
                    packages,
                    lastLargerPackageIndex,
                    currentIndex
                )
            }
        }

        val pivotIndex = lastLargerPackageIndex + 1

        swapPackages(
            packages,
            pivotIndex,
            endIndex
        )

        return pivotIndex
    }

    private fun swapPackages(
        packages: MutableList<Package>,
        firstIndex: Int,
        secondIndex: Int
    ) {
        val temp = packages[firstIndex]
        packages[firstIndex] = packages[secondIndex]
        packages[secondIndex] = temp
    }
}