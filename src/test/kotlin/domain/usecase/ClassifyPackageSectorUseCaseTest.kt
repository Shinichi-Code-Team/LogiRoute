package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.PackageRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ClassifyPackageSectorUseCaseTest {

    private val packageRepository = mockk<PackageRepository>()
    private val useCase = ClassifyPackageSectorUseCase(packageRepository)

    private fun createWarehouse(id: String, zone: String, lat: Double, lon: Double) =
        Warehouse(id = id, name = "Warehouse $id", regionalZone = zone, latitude = lat, longitude = lon)

    private fun createPackage(id: String, weight: Double, warehouse: Warehouse) =
        Package(
            id = id,
            weight = weight,
            origin = warehouse,
            destination = warehouse,
            priority = Priority.STANDARD
        )

    @Test
    fun `invoke classifies package sector based on majority vote of 5 nearest neighbors`() = runBlocking {
        // Given
        val targetWh = createWarehouse("WH-TARGET", "CENTRAL", 31.95, 35.91)
        val targetPkg = createPackage("PKG-TARGET", 15.0, targetWh)

        val northWh = createWarehouse("WH-N", "NORTH", 31.96, 35.92)
        val northPkgs = (1..5).map { createPackage("PKG-N-$it", 15.2, northWh) }

        val southWh = createWarehouse("WH-S", "SOUTH", 30.10, 32.50)
        val southPkgs = (1..2).map { createPackage("PKG-S-$it", 10.0, southWh) }

        val allPackages = listOf(targetPkg) + northPkgs + southPkgs
        coEvery { packageRepository.getAllPackages() } returns allPackages

        // When
        val result = useCase("PKG-TARGET")

        // Then
        assertTrue(result.isSuccess)
        assertEquals("NORTH", result.getOrNull())
    }

    @Test
    fun `invoke returns failure when package is not found`() = runBlocking {
        // Given
        coEvery { packageRepository.getAllPackages() } returns emptyList()

        // When
        val result = useCase("NON-EXISTENT")

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `invoke returns failure when history dataset has fewer than 5 records`() = runBlocking {
        // Given
        val wh = createWarehouse("WH-1", "NORTH", 31.0, 35.0)
        val targetPkg = createPackage("PKG-1", 10.0, wh)
        val otherPkg = createPackage("PKG-2", 10.0, wh)

        coEvery { packageRepository.getAllPackages() } returns listOf(targetPkg, otherPkg)

        // When
        val result = useCase("PKG-1")

        // Then
        assertTrue(result.isFailure)
    }
}
