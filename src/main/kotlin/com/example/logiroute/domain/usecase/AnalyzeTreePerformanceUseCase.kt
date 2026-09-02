package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.logic.algorithm.tree.BalancedBinarySearchTree
import com.example.logiroute.domain.logic.algorithm.tree.BinarySearchTree
import com.example.logiroute.domain.model.response.TreePerformanceReport

private const val TOTAL_PACKAGE_IDS = 1000

class AnalyzeTreePerformanceUseCase {

    operator fun invoke(): TreePerformanceReport {
        val sequentialIds = generateSequentialPackageIds()

        val unbalancedTree = BinarySearchTree()
        sequentialIds.forEach { id -> unbalancedTree.insert(id) }

        val balancedTree = BalancedBinarySearchTree()
        balancedTree.buildFromSorted(sequentialIds)

        val sampleKeys = sampleAcrossRange(sequentialIds)

        val unbalancedSteps = sampleKeys.associateWith { key -> unbalancedTree.searchWithStepCount(key) }
        val balancedSteps = sampleKeys.associateWith { key -> balancedTree.searchWithStepCount(key) }

        return TreePerformanceReport(
            sampleKeys = sampleKeys,
            unbalancedSteps = unbalancedSteps,
            balancedSteps = balancedSteps,
            unbalancedHeight = unbalancedTree.height(),
            balancedHeight = balancedTree.height()
        )
    }

    private fun generateSequentialPackageIds(): List<String> {
        return (1..TOTAL_PACKAGE_IDS).map { index ->
            "PKG-" + index.toString().padStart(6, '0')
        }
    }

    private fun sampleAcrossRange(ids: List<String>): List<String> {
        val sampleIndices = listOf(0, ids.size / 4, ids.size / 2, (ids.size * 3) / 4, ids.size - 1)
        return sampleIndices.map { index -> ids[index] }
    }
}