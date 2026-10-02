package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.algorithm.sorting.PackageSelectionSort
import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.request.ConsolidationOpportunityRequest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PrioritizeShipmentConsolidationUseCaseTest {

    private val packageSelectionSort: PackageSelectionSort = mockk()
    private val useCase = PrioritizeShipmentConsolidationUseCase(packageSelectionSort)

    private val warehouseA = Warehouse("WH-001", "Central Hub", "NORTH", 31.5, 34.5)
    private val warehouseB = Warehouse("WH-002", "East Hub", "EAST", 31.6, 34.6)

    @Test
    fun `should prioritize packages in consolidation opportunity`() {
        // Given
        val mainPackage = Package("PKG-000001", 50.0, warehouseA, warehouseB, Priority.STANDARD)
        val compatiblePackage = Package("PKG-000002", 30.0, warehouseA, warehouseB, Priority.URGENT)

        val opportunity = ConsolidationOpportunityRequest(
            mainPackage = mainPackage,
            compatiblePackages = listOf(compatiblePackage),
            sharedRoute = listOf(warehouseA, warehouseB)
        )

        val expectedSortedPackages = listOf(compatiblePackage, mainPackage)

        every { 
            packageSelectionSort.sortPackagesByPriorityConsideringWeight(listOf(mainPackage, compatiblePackage)) 
        } returns expectedSortedPackages

        // When
        val result = useCase(opportunity)

        // Then
        assertEquals(expectedSortedPackages, result)
    }
}
