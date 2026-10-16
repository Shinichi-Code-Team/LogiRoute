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

    private val centralWarehouse = Warehouse("WH-001", "Hub-001", "CENTRAL", 37.91, -88.46)
    private val westWarehouse = Warehouse("WH-002", "Hub-002", "WEST", 29.64, -99.44)

    @Test
    fun `should classify package sector successfully when sufficient historical data exists`() = runBlocking {
        val targetPackage = Package("PKG-000001", 10.0, centralWarehouse, centralWarehouse, Priority.STANDARD)
        val history = listOf(
            Package("PKG-000002", 10.5, centralWarehouse, centralWarehouse, Priority.STANDARD),
            Package("PKG-000003", 11.0, centralWarehouse, centralWarehouse, Priority.STANDARD),
            Package("PKG-000004", 10.2, centralWarehouse, centralWarehouse, Priority.STANDARD),
            Package("PKG-000005", 50.0, westWarehouse, westWarehouse, Priority.URGENT),
            Package("PKG-000006", 55.0, westWarehouse, westWarehouse, Priority.URGENT)
        )
        coEvery { packageRepository.getAllPackages() } returns (listOf(targetPackage) + history)

        val result = useCase("PKG-000001")

        assertEquals("CENTRAL", result.getOrNull())
    }

    @Test
    fun `should classify package sector correctly when custom k parameter is specified`() = runBlocking {
        val targetPackage = Package("PKG-000001", 10.0, centralWarehouse, centralWarehouse, Priority.STANDARD)
        val history = listOf(
            Package("PKG-000002", 10.5, centralWarehouse, centralWarehouse, Priority.STANDARD),
            Package("PKG-000003", 11.0, centralWarehouse, centralWarehouse, Priority.STANDARD),
            Package("PKG-000004", 50.0, westWarehouse, westWarehouse, Priority.URGENT)
        )
        coEvery { packageRepository.getAllPackages() } returns (listOf(targetPackage) + history)

        val result = useCase(packageId = "PKG-000001", k = 3)

        assertEquals("CENTRAL", result.getOrNull())
    }

    @Test
    fun `should return failure when package id does not exist in repository`() = runBlocking {
        coEvery { packageRepository.getAllPackages() } returns emptyList()

        val result = useCase("PKG-999999")

        assertTrue(result.isFailure)
    }

    @Test
    fun `should return failure when historical records are fewer than k`() = runBlocking {
        val targetPackage = Package("PKG-000001", 10.0, centralWarehouse, centralWarehouse, Priority.STANDARD)
        val history = listOf(
            Package("PKG-000002", 10.5, centralWarehouse, centralWarehouse, Priority.STANDARD)
        )
        coEvery { packageRepository.getAllPackages() } returns (listOf(targetPackage) + history)

        val result = useCase("PKG-000001")

        assertTrue(result.isFailure)
    }

    @Test
    fun `should handle identical coordinate features without division by zero error`() = runBlocking {
        val targetPackage = Package("PKG-000001", 10.0, centralWarehouse, centralWarehouse, Priority.STANDARD)
        val identicalHistory = List(5) { index ->
            Package("PKG-00000${index + 2}", 10.0, centralWarehouse, centralWarehouse, Priority.STANDARD)
        }
        coEvery { packageRepository.getAllPackages() } returns (listOf(targetPackage) + identicalHistory)

        val result = useCase("PKG-000001")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `should return failure when package repository throws exception`() = runBlocking {
        coEvery { packageRepository.getAllPackages() } throws RuntimeException("Database error")

        val result = useCase("PKG-000001")

        assertTrue(result.isFailure)
    }
}
