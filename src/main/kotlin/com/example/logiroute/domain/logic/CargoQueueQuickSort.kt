package com.example.logiroute.domain.logic

import com.example.logiroute.data.dataholder.PackageRaw
import  com.example.logiroute.domain.model.Package

fun sortByWeightDescending(packages: MutableList<Package>) {
    quickSort(packages, startIndex = 0, endIndex = packages.lastIndex)
}

private fun quickSort(
    packages: MutableList<Package>,
    startIndex: Int,
    endIndex: Int
) {
    if (startIndex >= endIndex) {
        return
    }
    val pivotIndex = partition(
        packages,
        startIndex,
        endIndex
    )
    quickSort(
        packages,
        startIndex,
        pivotIndex - 1
    )

    quickSort(
        packages,
        pivotIndex + 1,
        endIndex
    )
}

private fun partition(
    packages: MutableList<Package>,
    startIndex: Int,
    endIndex: Int
): Int {

    val pivotWeight = packages[endIndex].weight
    var lastSortedIndex = startIndex - 1

    for (currentIndex in startIndex until endIndex) {

        val shouldMoveBeforePivot =
            packages[currentIndex].weight > pivotWeight

        if (shouldMoveBeforePivot) {

            lastSortedIndex++

            swapPackages(
                packages,
                lastSortedIndex,
                currentIndex
            )
        }
    }
    val pivotIndex = lastSortedIndex + 1
    swapPackages(
        packages,
        pivotIndex,
        endIndex
    )

    return pivotIndex
}

