package com.example.logiroute.logic

import com.example.logiroute.data.dataholder.PackageRaw

fun comparePackagePriority(
    selectedPackage: PackageRaw,
    currentPackage: PackageRaw
): Int {
    return selectedPackage.priority.comparePriority(currentPackage.priority)
}

fun comparePackageWeight(
    selectedPackage: PackageRaw,
    currentPackage: PackageRaw
): Int {
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
    packages: List<PackageRaw>
): List<PackageRaw> {
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
    sortedPosition: Int
): Int {
    var selectedPackageIndex = sortedPosition

    for (currentPackageIndex in sortedPosition + 1..packages.lastIndex) {
        val priorityComparison = comparePackagePriority(
            packages[selectedPackageIndex],
            packages[currentPackageIndex]
        )

        when {
            priorityComparison < 0 -> {
                selectedPackageIndex = currentPackageIndex
            }

            priorityComparison == 0 -> {
                val weightComparison = comparePackageWeight(
                    packages[selectedPackageIndex],
                    packages[currentPackageIndex]
                )

                if (weightComparison < 0) {
                    selectedPackageIndex = currentPackageIndex
                }
            }
        }
    }

    return selectedPackageIndex
}