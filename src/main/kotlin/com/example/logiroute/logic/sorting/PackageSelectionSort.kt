package com.example.logiroute.logic.sorting

import com.example.logiroute.dataholder.PackageRow

fun comparePackagePriority(
    selectedPackage: PackageRow,
    currentPackage: PackageRow
): Int {
    return selectedPackage.priority.compareTo(currentPackage.priority)
}

fun comparePackageWeight(
    selectedPackage: PackageRow,
    currentPackage: PackageRow
): Int {
    return selectedPackage.weight.compareTo(currentPackage.weight)
}

fun swapPackages(
    packages: MutableList<PackageRow>,
    firstPackageIndex: Int,
    secondPackageIndex: Int
) {
    val tempPackage = packages[firstPackageIndex]
    packages[firstPackageIndex] = packages[secondPackageIndex]
    packages[secondPackageIndex] = tempPackage
}

fun sortPackagesByPriority(packages: MutableList<PackageRow>) {

    for (sortedPosition in 0 until packages.lastIndex) {

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

        if (selectedPackageIndex != sortedPosition) {
            swapPackages(
                packages,
                selectedPackageIndex,
                sortedPosition
            )
        }
    }
}