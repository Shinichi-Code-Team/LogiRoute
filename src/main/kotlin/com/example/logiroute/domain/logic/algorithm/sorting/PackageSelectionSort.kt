package com.example.logiroute.domain.logic.algorithm.sorting

import com.example.logiroute.domain.model.*

private fun comparePackageByPriority(
    selectedPackage: Package,
    currentPackage: Package
): Int {
    return selectedPackage.priority.comparePriority(currentPackage.priority)
}

private fun comparePackageByWeight(
    selectedPackage: Package,
    currentPackage: Package
): Int {
    return selectedPackage.compareWeight(currentPackage)
}

fun swapPackages(
    packages: MutableList<Package>,
    firstPackageIndex: Int,
    secondPackageIndex: Int
) {
    val tempPackage = packages[firstPackageIndex]
    packages[firstPackageIndex] = packages[secondPackageIndex]
    packages[secondPackageIndex] = tempPackage
}

fun sortPackagesByPriorityConsideringWeight(
    packages: List<Package>
): List<Package> {
    val sortedPackages = packages.toMutableList()

    for (sortedPosition in 0 until sortedPackages.lastIndex) {
        val selectedPackageIndex = findHighestPriorityPackageIndex(
            sortedPackages,
            sortedPosition
        )

        if (selectedPackageIndex != sortedPosition) {
            swapPackages(
                sortedPackages,
                selectedPackageIndex,
                sortedPosition
            )
        }
    }

    return sortedPackages
}

private fun findHighestPriorityPackageIndex(
    packages: MutableList<Package>,
    sortedPosition: Int
): Int {
    var selectedPackageIndex = sortedPosition

    for (currentPackageIndex in sortedPosition + 1..packages.lastIndex) {
        val selectedPackage = packages[selectedPackageIndex]
        val candidatePackage = packages[currentPackageIndex]

        if (hasHigherPackagePriority(selectedPackage, candidatePackage)) {
            selectedPackageIndex = currentPackageIndex
        }
    }

    return selectedPackageIndex
}

private fun hasHigherPackagePriority(
    selectedPackage: Package,
    candidatePackage: Package
): Boolean {
    val priorityComparison = comparePackageByPriority(selectedPackage, candidatePackage)

    if (isLowerPriority(priorityComparison)) {
        return true
    }

    if (isSamePriorityWithLowerWeight(priorityComparison, selectedPackage, candidatePackage)) {
        return true
    }

    return false
}

private fun isLowerPriority(priorityComparison: Int): Boolean {
    return priorityComparison < 0
}

private fun isSamePriorityWithLowerWeight(
    priorityComparison: Int,
    selectedPackage: Package,
    candidatePackage: Package
): Boolean {
    if (priorityComparison != 0) return false

    val weightComparison = comparePackageByWeight(selectedPackage, candidatePackage)
    return weightComparison < 0
}