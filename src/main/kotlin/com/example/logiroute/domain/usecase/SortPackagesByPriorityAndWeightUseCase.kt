package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package

class SortPackagesByPriorityAndWeightUseCase {

    operator fun invoke(packages: List<Package>): List<Package> =
        selectionSort(packages)

    private fun selectionSort(packages: List<Package>): List<Package> {
        if (packages.isEmpty()) return emptyList()

        val selected = packages.withIndex().minWithOrNull(
            compareByDescending<IndexedValue<Package>> {
                it.value.priority
            }.thenBy {
                it.value.weight
            }
        ) ?: return emptyList()

        val remainingPackages = packages.filterIndexed { index, _ ->
            index != selected.index
        }

        return listOf(selected.value) + selectionSort(remainingPackages)
    }
}