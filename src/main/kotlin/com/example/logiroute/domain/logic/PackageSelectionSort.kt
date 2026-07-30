package com.example.logiroute.domain.logic

import com.example.logiroute.data.dataholder.PackageRaw

fun comparePackageByPriority(
    selectedPackage: PackageRaw,
    currentPackage: PackageRaw): Int {
    return selectedPackage.priority.comparePriority(currentPackage.priority)
}

fun comparePackageByWeight(
    selectedPackage: PackageRaw,
    currentPackage: PackageRaw): Int {
    return selectedPackage.compareWeight(currentPackage)
}

fun swapPackages(
    packages: MutableList<PackageRaw>,
    firstPackageIndex: Int,
    secondPackageIndex: Int
) {
    val tempPackage = packages[firstPackageIndex]
    packages[firstPackageIndex] = packages[secondPackageIndex]
    packages[secondPackageIndex] = tempPackage
}

fun sortPackagesByPriorityConsideringWeight(
    packages: List<PackageRaw>): List<PackageRaw> {
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

fun findHighestPriorityPackageIndex(
    packages: MutableList<PackageRaw>,
    sortedPosition: Int): Int {
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

fun hasHigherPackagePriority(
    selectedPackage: PackageRaw,
    candidatePackage: PackageRaw): Boolean {
    val priorityComparison = comparePackageByPriority(selectedPackage, candidatePackage)

    if (isLowerPriority(priorityComparison)) {
        return true
    }

    if (isSamePriorityWithLowerWeight(priorityComparison, selectedPackage, candidatePackage)) {
        return true
    }

    return false
}

fun isLowerPriority(priorityComparison: Int): Boolean {
    return priorityComparison < 0
}

fun isSamePriorityWithLowerWeight(
    priorityComparison: Int,
    selectedPackage: PackageRaw,
    candidatePackage: PackageRaw): Boolean {
    if (priorityComparison != 0) return false

    val weightComparison = comparePackageByWeight(selectedPackage, candidatePackage)
    return weightComparison < 0
}