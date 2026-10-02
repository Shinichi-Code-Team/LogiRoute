package com.example.logiroute.domain.usecase

import com.example.logiroute.com.example.logiroute.domain.usecase.AssignPackagesToVehiclesUseCase
import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AssignPackagesToVehiclesUseCaseTest {

    private val useCase = AssignPackagesToVehiclesUseCase()

    private val warehouseA = Warehouse("WH-001", "Central Hub", "NORTH", 31.5, 34.5)
    private val warehouseB = Warehouse("WH-002", "East Hub", "EAST", 31.6, 34.6)

    private val vehicle1 = Vehicle("TRK-0001", 1000.0, 2.5, warehouseA)
    private val vehicle2 = Vehicle("TRK-0002", 800.0, 2.0, warehouseA)

    @Test
    fun `should assign packages to vehicles using assignment ring`() {
        // Given
        val package1 = Package("PKG-000001", 50.0, warehouseA, warehouseB, Priority.STANDARD)
        val package2 = Package("PKG-000002", 30.0, warehouseA, warehouseB, Priority.URGENT)
        val packages = listOf(package1, package2)
        val vehicles = listOf(vehicle1, vehicle2)

        // When
        val result = useCase(packages, vehicles)

        // Then
        assertNotNull(result)
        assertEquals(2, result.keys.size)
        assertTrue(result.containsKey(vehicle1))
        assertTrue(result.containsKey(vehicle2))
        val totalAssignedPackages = result.values.sumOf { it.size }
        assertEquals(2, totalAssignedPackages)
    }

    @Test
    fun `should return empty package lists for vehicles when no packages provided`() {
        // Given
        val packages = emptyList<Package>()
        val vehicles = listOf(vehicle1, vehicle2)

        // When
        val result = useCase(packages, vehicles)

        // Then
        assertEquals(2, result.keys.size)
        assertTrue(result.getValue(vehicle1).isEmpty())
        assertTrue(result.getValue(vehicle2).isEmpty())
    }
}
