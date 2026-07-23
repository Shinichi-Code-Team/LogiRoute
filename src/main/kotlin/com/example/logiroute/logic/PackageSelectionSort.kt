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
    return selectedPackage.weight.compareTo(currentPackage.weight)
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
    packages: MutableList<PackageRaw>
) {
    for (sortedPosition in 0 until packages.lastIndex) {
        val selectedPackageIndex = findHighestPriorityPackageIndex(
            packages,
            sortedPosition
        )
        if (selectedPackageIndex != sortedPosition) {
            swapPackages(
                packages,
                selectedPackageIndex,
                sortedPosition
            )
        }
    }
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