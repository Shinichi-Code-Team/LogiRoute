package com.example.logiroute.domain.logic.algorithm.sorting

import com.example.logiroute.domain.model.Package

fun sortByWeightDescending(packages: MutableList<Package>) {
    if (packages.size < 2) return
    quickSort(packages, packages.indices.first, endIndex = packages.lastIndex)
}

private fun quickSort(packages: MutableList<Package>, startIndex: Int, endIndex: Int) {
    if (startIndex >= endIndex) return
    val pivotFinalIndex = partition(packages, startIndex, endIndex)
    val leftPartitionEndIndex = pivotFinalIndex - 1
    val rightPartitionStartIndex = pivotFinalIndex + 1
    quickSort(packages, startIndex, leftPartitionEndIndex)
    quickSort(packages, rightPartitionStartIndex, endIndex)
}

private fun partition(packages: MutableList<Package>, startIndex: Int, endIndex: Int): Int {
    val pivotWeight = packages[endIndex].weight
    var lastLargerPackageIndex = startIndex - 1
    for (currentIndex in startIndex until endIndex) {
        val shouldMoveBeforePivot = packages[currentIndex].weight > pivotWeight
        if (shouldMoveBeforePivot) {
            lastLargerPackageIndex++
            swapPackages(packages, lastLargerPackageIndex, currentIndex)
        }
    }
    val pivotIndex = lastLargerPackageIndex + 1
    swapPackages(packages, pivotIndex, endIndex)

    return pivotIndex
}

