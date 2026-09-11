package com.example.logiroute.domain.usecase

import com.example.logiroute.com.example.logiroute.domain.model.result.TreePerformanceReport
import com.example.logiroute.domain.algorithm.tree.BalancedBinarySearchTree
import com.example.logiroute.domain.algorithm.tree.BinarySearchTree

private const val TOTAL_PACKAGE_IDS = 1000

class AnalyzeTreePerformanceUseCase {
    private companion object {
        const val TOTAL_PACKAGE_IDS = 1000
        const val SAMPLE_QUARTER_DIVISOR = 4
        const val SAMPLE_HALF_DIVISOR = 2
        const val SAMPLE_THREE_QUARTERS_MULTIPLIER = 3
    }

    operator fun invoke(): TreePerformanceReport {
        val sequentialIds = generateSequentialPackageIds()

        val unbalancedTree = BinarySearchTree()
        sequentialIds.forEach { id ->
            unbalancedTree.insert(id)
        }

        val balancedTree = BalancedBinarySearchTree()
        balancedTree.buildFromSorted(sequentialIds)

        val sampleKeys = sampleAcrossRange(sequentialIds)

        val unbalancedSteps =
            sampleKeys.associateWith { key ->
                unbalancedTree.searchWithStepCount(key)
            }

        val balancedSteps =
            sampleKeys.associateWith { key ->
                balancedTree.searchWithStepCount(key)
            }

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
        val sampleIndices = listOf(
            0,
            ids.size / SAMPLE_QUARTER_DIVISOR,
            ids.size / SAMPLE_HALF_DIVISOR,
            (ids.size * SAMPLE_THREE_QUARTERS_MULTIPLIER) /
                    SAMPLE_QUARTER_DIVISOR,
            ids.size - 1
        )

        return sampleIndices.map { index ->
            ids[index]
        }
    }
}