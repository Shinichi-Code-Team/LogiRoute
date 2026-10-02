package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.request.ConsolidationOpportunityRequest
import com.example.logiroute.domain.repository.PackageRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DetectShipmentConsolidationOpportunitiesUseCaseTest {

    private val packageRepository: PackageRepository = mockk()
    private val findOptimalPathUseCase: FindOptimalPathUseCase = mockk()

    private val useCase = DetectShipmentConsolidationOpportunitiesUseCase(
        packageRepository = packageRepository,
        findOptimalPathUseCase = findOptimalPathUseCase
    )

    private val warehouseA = Warehouse("WH-001", "Central Hub", "NORTH", 31.5, 34.5)
    private val warehouseB = Warehouse("WH-002", "East Hub", "EAST", 31.6, 34.6)
    private val warehouseC = Warehouse("WH-003", "South Hub", "SOUTH", 31.7, 34.7)

    @Test
    fun `should detect consolidation opportunities for packages with shared routes`() = runTest {
        // Given
        val mainPackage = Package("PKG-000001", 50.0, warehouseA, warehouseC, Priority.STANDARD)
        val candidatePackage = Package("PKG-000002", 30.0, warehouseA, warehouseB, Priority.URGENT)
        val allPackages = listOf(mainPackage, candidatePackage)

        val mainRoute = listOf(warehouseA, warehouseB, warehouseC)
        val candidateRoute = listOf(warehouseA, warehouseB)

        coEvery { packageRepository.getAllPackages() } returns allPackages
        coEvery { findOptimalPathUseCase(source = warehouseA, destination = warehouseC) } returns mainRoute
        coEvery { findOptimalPathUseCase(source = warehouseA, destination = warehouseB) } returns candidateRoute

        // When
        val opportunities = useCase(warehouseA)

        // Then
        assertTrue(opportunities.isNotEmpty())
        val opportunity = opportunities.first()
        assertEquals(mainPackage, opportunity.mainPackage)
        assertEquals(listOf(candidatePackage), opportunity.compatiblePackages)
        assertEquals(mainRoute, opportunity.sharedRoute)
    }

    @Test
    fun `should return empty list when no packages exist for warehouse`() = runTest {
        // Given
        coEvery { packageRepository.getAllPackages() } returns emptyList()

        // When
        val opportunities = useCase(warehouseA)

        // Then
        assertTrue(opportunities.isEmpty())
    }
}
