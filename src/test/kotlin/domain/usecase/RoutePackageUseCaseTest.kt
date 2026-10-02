package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.repository.WarehouseRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class RoutePackageUseCaseTest {

    private val packageRepository: PackageRepository = mockk()
    private val warehouseRepository: WarehouseRepository = mockk()

    private val useCase = RoutePackageUseCase(packageRepository, warehouseRepository)

    private val warehouseA = Warehouse("WH-001", "Central Hub", "NORTH", 31.5, 34.5)
    private val warehouseB = Warehouse("WH-002", "East Hub", "EAST", 31.6, 34.6)
    private val warehouseC = Warehouse("WH-003", "West Hub", "WEST", 31.7, 34.7)

    private val packageItem = Package("PKG-000001", 50.0, warehouseA, warehouseB, Priority.STANDARD)

    @Test
    fun `should reroute package successfully to new destination`() = runTest {
        // Given
        warehouseA.addPackage(packageItem)
        coEvery { packageRepository.getAllPackages() } returns listOf(packageItem)
        coEvery { warehouseRepository.getAllWarehouses() } returns listOf(warehouseA, warehouseB, warehouseC)

        // When
        val updatedPackage = useCase("PKG-000001", "WH-003")

        // Then
        assertEquals("WH-003", updatedPackage.destination.id)
    }

    @Test
    fun `should throw IllegalArgumentException when package is not found`() = runTest {
        // Given
        coEvery { packageRepository.getAllPackages() } returns emptyList()

        // When & Then
        assertThrows<IllegalArgumentException> {
            useCase("PKG-999999", "WH-003")
        }
    }

    @Test
    fun `should throw IllegalArgumentException when new destination warehouse is not found`() = runTest {
        // Given
        coEvery { packageRepository.getAllPackages() } returns listOf(packageItem)
        coEvery { warehouseRepository.getAllWarehouses() } returns listOf(warehouseA, warehouseB)

        // When & Then
        assertThrows<IllegalArgumentException> {
            useCase("PKG-000001", "WH-999")
        }
    }

    @Test
    fun `should throw IllegalArgumentException when rerouting to same destination`() = runTest {
        // Given
        coEvery { packageRepository.getAllPackages() } returns listOf(packageItem)
        coEvery { warehouseRepository.getAllWarehouses() } returns listOf(warehouseA, warehouseB)

        // When & Then
        assertThrows<IllegalArgumentException> {
            useCase("PKG-000001", "WH-002")
        }
    }
}
